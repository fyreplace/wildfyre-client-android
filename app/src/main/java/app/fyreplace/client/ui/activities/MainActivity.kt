package app.fyreplace.client.ui.activities

import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.core.view.children
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import app.fyreplace.client.AppGlide
import app.fyreplace.client.FyreplaceApplication
import app.fyreplace.client.NavigationMainDirections
import app.fyreplace.client.R
import app.fyreplace.client.data.models.ImageData
import app.fyreplace.client.databinding.*
import app.fyreplace.client.ui.ImageSelector
import app.fyreplace.client.viewmodels.MainActivityViewModel
import app.fyreplace.client.viewmodels.lazyViewModel
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_app_bar.*
import java.io.ByteArrayInputStream

/**
 * The central activity that hosts the different fragments.
 */
class MainActivity : FailureHandlingActivity(), NavController.OnDestinationChangedListener,
    DrawerLayout.DrawerListener, ImageSelector {
    override val viewModel by lazyViewModel<MainActivityViewModel>()
    override val contextWrapper = this
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var toolbarInset: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setBackgroundDrawable(null)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = ActivityCompat.getColor(this, R.color.navigation)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

            if (nightMode == Configuration.UI_MODE_NIGHT_NO) {
                drawer_layout.systemUiVisibility =
                    drawer_layout.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        }

        val navHeaderBinding = MainNavHeaderBinding.bind(navigation_view.getHeaderView(0))
            .apply { lifecycleOwner = this@MainActivity; model = viewModel }
        MainNavActionsNotificationsBinding.bind(navigation_view.menu.findItem(R.id.fragment_notifications).actionView)
            .run { lifecycleOwner = this@MainActivity; model = viewModel }
        MainNavActionsThemeBinding.bind(navigation_view.menu.findItem(R.id.settings_theme_selector).actionView)
            .run { lifecycleOwner = this@MainActivity; model = viewModel }
        MainNavActionsBadgeBinding.bind(navigation_view.menu.findItem(R.id.settings_badge_toggle).actionView)
            .run { lifecycleOwner = this@MainActivity; model = viewModel }
        MainAppBarBinding.bind(content)
            .run { lifecycleOwner = this@MainActivity; model = viewModel }

        viewModel.uiRefreshTick.observe(this) { launchCatching { viewModel.updateNotificationCount() } }

        viewModel.isLogged.observe(this) {
            if (!it) {
                val navController = findNavController(R.id.navigation_host)

                if (navController.currentDestination?.id != R.id.fragment_login) {
                    navController.navigate(
                        if (viewModel.startupLogin)
                            NavigationMainDirections.actionGlobalFragmentLoginStartup()
                        else
                            NavigationMainDirections.actionGlobalFragmentLogin()
                    )
                }
            }
        }

        viewModel.selectedThemeIndex.observe(this) {
            AppCompatDelegate.setDefaultNightMode(MainActivityViewModel.THEMES[it])
            delegate.applyDayNight()
        }

        viewModel.userAvatar.observe(this) {
            AppGlide.with(this)
                .load(it)
                .transform(
                    CenterCrop(),
                    RoundedCorners(resources.getDimensionPixelOffset(R.dimen.nav_header_user_picture_rounding))
                )
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(navHeaderBinding.userPicture)
        }

        val hostFragment = supportFragmentManager.findFragmentById(R.id.navigation_host) as NavHostFragment

        viewModel.postInfo.observe(this) { info ->
            if (hostFragment.navController.currentDestination?.id in NO_TITLE_DESTINATIONS) {
                setTitleInfo(info)
            }
        }

        setSupportActionBar(toolbar)
        drawer_layout.addDrawerListener(this)

        appBarConfiguration = AppBarConfiguration(TOP_LEVEL_DESTINATIONS, drawer_layout)
        toolbarInset = toolbar.contentInsetStartWithNavigation

        setupActionBarWithNavController(hostFragment.navController, appBarConfiguration)
        navigation_view.setupWithNavController(hostFragment.navController)
        hostFragment.navController.addOnDestinationChangedListener(this)

        MainActivityViewModel.NAVIGATION_LINKS.forEach { pair ->
            navigation_view.menu.findItem(pair.key).setOnMenuItemClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(pair.value)))
                return@setOnMenuItemClickListener true
            }
        }

        navigation_view.menu.findItem(R.id.fyreplace_logout).setOnMenuItemClickListener {
            viewModel.logout()
            return@setOnMenuItemClickListener true
        }

        navigation_view.doOnLayout {
            navigation_view.findViewById<View>(R.id.edit)?.setOnClickListener { editProfile() }
        }

        toolbar.doOnLayout {
            badge.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = toolbar.height / 2
                topMargin = toolbar.height / 2 - resources.getDimensionPixelOffset(R.dimen.margin_medium)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent == null) {
            return
        }

        val uri = intent.data?.path.orEmpty()

        for (pair in REGEX_TO_DIRECTIONS) {
            pair.key.matchEntire(uri)?.let {
                findNavController(R.id.navigation_host).navigate(pair.value(it))
                return
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
        item!!.onNavDestinationSelected(findNavController(R.id.navigation_host))

    override fun onBackPressed() = when {
        drawer_layout.isDrawerOpen(GravityCompat.START) -> drawer_layout.closeDrawer(GravityCompat.START)
        else -> super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean =
        findNavController(R.id.navigation_host).navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<FailureHandlingActivity>.onActivityResult(requestCode, resultCode, data)
        super<ImageSelector>.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        drawer_layout.setDrawerLockMode(
            if (destination.id in TOP_LEVEL_DESTINATIONS)
                DrawerLayout.LOCK_MODE_UNLOCKED
            else
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        )

        when {
            destination.id == R.id.fragment_login -> toolbar.navigationIcon = null
            toolbar.navigationIcon == null -> toolbar.navigationIcon =
                DrawerArrowDrawable(this).apply { isSpinEnabled = true }
        }

        viewModel.setNotificationBadgeVisible(
            destination.id != R.id.fragment_notifications
                && destination.id in TOP_LEVEL_DESTINATIONS
        )

        if (destination.id in NO_TITLE_DESTINATIONS && toolbar.title.toString() == getString(R.string.app_name)) {
            toolbar.title = ""
        }

        toolbar.subtitle = ""
        toolbar.logo = null
        toolbar.contentInsetStartWithNavigation = toolbarInset
        toolbar.setTitleTextAppearance(this, R.style.AppTheme_TextAppearance_ActionBar_Title)
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) = Unit

    override fun onDrawerOpened(drawerView: View) {
        launchCatching { viewModel.updateNotificationCount() }
    }

    override fun onDrawerClosed(drawerView: View) = Unit

    override fun onDrawerStateChanged(newState: Int) = Unit

    override fun onImage(image: ImageData) = viewModel.setPendingProfileAvatar(image)

    private fun setTitleInfo(info: MainActivityViewModel.PostInfo?) {
        if (info == null) {
            toolbar.title = ""
            toolbar.subtitle = ""
            toolbar.logo = null
            return
        }

        toolbar.collapseActionView()
        toolbar.title = " " + (info.author?.name ?: getString(R.string.main_author_anonymous))
        toolbar.subtitle = " " + info.date
        toolbar.contentInsetStartWithNavigation = 0
        toolbar.setTitleTextAppearance(this, R.style.AppTheme_TextAppearance_ActionBar_Title_Condensed)

        val size = resources.getDimensionPixelOffset(R.dimen.toolbar_logo_picture_size)

        if (info.author != null) {
            AppGlide.with(this)
                .load(info.author.avatar ?: R.drawable.default_avatar)
                .placeholder(android.R.color.transparent)
                .transition(IMAGE_TRANSITION)
                .transform(LOGO_TRANSFORM)
                .into(object : CustomTarget<Drawable>(size, size) {
                    override fun onLoadCleared(placeholder: Drawable?) = Unit

                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        toolbar.logo = resource
                        toolbar.children
                            .filter { it is ImageView }
                            .map { it as ImageView }
                            .find { it.drawable == resource }
                            ?.setOnClickListener {
                                findNavController(R.id.navigation_host)
                                    .navigate(NavigationMainDirections.actionGlobalFragmentUser(author = info.author))
                            }
                    }
                })
        } else {
            toolbar.logo = null
        }
    }

    /**
     * Shows a dialog to let the user edit their profile bio and their avatar.
     */
    private fun editProfile() {
        lateinit var dialog: AlertDialog

        dialog = AlertDialog.Builder(this)
            .setView(R.layout.profile_editor)
            .setNegativeButton(R.string.cancel) { _: DialogInterface, _: Int ->
                viewModel.newUserAvatar.removeObservers(this)
                viewModel.resetPendingProfileAvatar()
            }
            .setPositiveButton(R.string.ok) { _: DialogInterface, _: Int ->
                viewModel.newUserAvatar.removeObservers(this)
                launchCatching {
                    viewModel.sendProfile(dialog.findViewById<TextView>(R.id.user_bio)!!.text.toString())
                }
            }
            .create()
            .apply { show() }

        val avatar = dialog.findViewById<ImageView>(R.id.user_picture)!!

        AppGlide.with(this)
            .load(viewModel.userAvatar.value)
            .transition(IMAGE_TRANSITION)
            .transform(AVATAR_TRANSFORM)
            .into(avatar)

        viewModel.newUserAvatar.observe(this) {
            AppGlide.with(this@MainActivity)
                .load(Drawable.createFromStream(ByteArrayInputStream(it?.bytes), "avatar"))
                .transition(IMAGE_TRANSITION)
                .transform(AVATAR_TRANSFORM)
                .into(avatar)
        }

        viewModel.userBio.value?.let {
            val bioEdit = dialog.findViewById<EditText>(R.id.user_bio)!!
            bioEdit.setText(it)

            if (it.isNotEmpty()) {
                bioEdit.minLines = 0
            }
        }
    }

    fun onSelectAvatarImageClicked(view: View) {
        (view.tag as? String)?.toInt()?.let { selectImage(it) }
    }

    private companion object {
        val TOP_LEVEL_DESTINATIONS = setOf(
            R.id.fragment_home,
            R.id.fragment_notifications,
            R.id.fragment_archive,
            R.id.fragment_own_posts
        )
        val NO_TITLE_DESTINATIONS = setOf(
            R.id.fragment_home,
            R.id.fragment_post,
            R.id.fragment_user
        )

        val POST_REGEX = Regex("/areas/(\\w+)/(\\d+)(?:/(\\d+))?")
        val USER_REGEX = Regex("/user/(\\d+)")
        val REGEX_TO_DIRECTIONS = mapOf(
            POST_REGEX to { result: MatchResult ->
                NavigationMainDirections.actionGlobalFragmentPost(
                    areaName = result.groupValues[1],
                    postId = result.groupValues[2].toLong(),
                    selectedCommentId = result.groupValues[3].takeIf { it.isNotEmpty() }?.toLong() ?: -1
                )
            },
            USER_REGEX to { result: MatchResult ->
                NavigationMainDirections.actionGlobalFragmentUser(
                    userId = result.groupValues[1].toLong()
                )
            }
        )

        val IMAGE_TRANSITION = DrawableTransitionOptions.withCrossFade()
        val AVATAR_TRANSFORM = MultiTransformation(
            CenterCrop(),
            RoundedCorners(FyreplaceApplication.context.resources.getDimensionPixelOffset(R.dimen.dialog_user_picture_rounding))
        )
        val LOGO_TRANSFORM = MultiTransformation(
            CenterCrop(),
            RoundedCorners(FyreplaceApplication.context.resources.getDimensionPixelOffset(R.dimen.toolbar_logo_picture_rounding))
        )
    }
}
