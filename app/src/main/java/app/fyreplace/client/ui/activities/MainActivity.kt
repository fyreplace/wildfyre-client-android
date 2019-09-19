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
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.Toolbar
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
import app.fyreplace.client.NavigationMainDirections
import app.fyreplace.client.R
import app.fyreplace.client.data.models.ImageData
import app.fyreplace.client.databinding.*
import app.fyreplace.client.ui.ImageSelector
import app.fyreplace.client.ui.fragments.BackHandlingFragment
import app.fyreplace.client.ui.fragments.FailureHandlingFragment
import app.fyreplace.client.ui.fragments.ToolbarUsingFragment
import app.fyreplace.client.viewmodels.MainActivityViewModel
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_app_bar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.ByteArrayInputStream

/**
 * The central activity that hosts the different fragments.
 */
class MainActivity : FailureHandlingActivity(R.layout.activity_main),
    NavController.OnDestinationChangedListener,
    DrawerLayout.DrawerListener, ImageSelector {
    override val viewModel by viewModel<MainActivityViewModel>()
    override val viewModelStoreOwner by lazy { this }
    override val contextWrapper = this
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val toolbarChangeListener by lazy { OnToolbarChangeListener(toolbar) }
    private var toolbarInset = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        val navController = findNavController(R.id.navigation_host)
        val navHeaderBinding = MainNavHeaderBinding.bind(navigation_view.getHeaderView(0))
            .apply { lifecycleOwner = this@MainActivity; model = viewModel }
        ActionMainNavFragmentNotificationsBinding.bind(navigation_view.menu.findItem(R.id.fragment_notifications).actionView)
            .run { lifecycleOwner = this@MainActivity; model = viewModel }
        ActionMainNavFragmentDraftsBinding.bind(navigation_view.menu.findItem(R.id.fragment_drafts).actionView)
            .run { lifecycleOwner = this@MainActivity; model = viewModel }
        ActionMainNavSettingsThemeSelectorBinding.bind(navigation_view.menu.findItem(R.id.settings_theme_selector).actionView)
            .run { lifecycleOwner = this@MainActivity; model = viewModel }
        ActionMainNavSettingsBadgeToggleBinding.bind(navigation_view.menu.findItem(R.id.settings_badge_toggle).actionView)
            .run { lifecycleOwner = this@MainActivity; model = viewModel }
        MainAppBarBinding.bind(content)
            .run { lifecycleOwner = this@MainActivity; model = viewModel }

        navigation_view.menu.findItem(R.id.fragment_drafts).actionView
            .findViewById<View>(R.id.button)
            .setOnClickListener {
                launch {
                    navController.navigate(
                        NavigationMainDirections.actionGlobalFragmentDraft(
                            draft = viewModel.createDraft(),
                            showHint = true
                        )
                    )
                }
            }

        viewModel.uiRefreshTick.observe(this) { launch { viewModel.updateNotificationCount() } }

        viewModel.isLogged.observe(this) {
            if (it) {
                launch { viewModel.updateProfileInfo() }
            } else if (navController.currentDestination?.id != R.id.fragment_login) {
                navController.navigate(
                    if (viewModel.startupLogin)
                        NavigationMainDirections.actionGlobalFragmentLoginStartup()
                    else
                        NavigationMainDirections.actionGlobalFragmentLogin()
                )
            }
        }

        viewModel.selectedThemeIndex.observe(this) {
            AppCompatDelegate.setDefaultNightMode(viewModel.getTheme(it))
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

        val hostFragment = supportFragmentManager
            .findFragmentById(R.id.navigation_host) as NavHostFragment

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

        toolbar.addOnLayoutChangeListener(toolbarChangeListener)
        toolbar.doOnLayout {
            badge.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = toolbar.height / 2
                topMargin = toolbar.height / 2 -
                    resources.getDimensionPixelOffset(R.dimen.margin_medium)
            }
        }
    }

    override fun onDestroy() {
        toolbar.removeOnLayoutChangeListener(toolbarChangeListener)
        super.onDestroy()
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

    override fun onOptionsItemSelected(item: MenuItem) =
        item.onNavDestinationSelected(findNavController(R.id.navigation_host))

    override fun onBackPressed() = when {
        drawer_layout.isDrawerOpen(GravityCompat.START) -> drawer_layout.closeDrawer(GravityCompat.START)
        currentFragmentAs<BackHandlingFragment>()?.onGoBack(BackHandlingFragment.Method.BACK_BUTTON) == false -> Unit
        else -> super.onBackPressed()
    }

    override fun onSupportNavigateUp() = when {
        currentFragmentAs<BackHandlingFragment>()?.onGoBack(BackHandlingFragment.Method.UP_BUTTON) == false -> false
        findNavController(R.id.navigation_host).navigateUp(appBarConfiguration) -> true
        else -> super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<FailureHandlingActivity>.onActivityResult(requestCode, resultCode, data)
        super<ImageSelector>.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        updateDrawer(destination)

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

    override fun onSupportActionModeStarted(mode: ActionMode) {
        super.onSupportActionModeStarted(mode)
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    override fun onSupportActionModeFinished(mode: ActionMode) {
        super.onSupportActionModeFinished(mode)
        findNavController(R.id.navigation_host).currentDestination?.let { updateDrawer(it) }
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) = Unit

    override fun onDrawerOpened(drawerView: View) {
        launch { viewModel.updateNotificationCount() }
    }

    override fun onDrawerClosed(drawerView: View) = Unit

    override fun onDrawerStateChanged(newState: Int) = Unit

    override fun onImage(image: ImageData) = viewModel.setPendingProfileAvatar(image)

    fun onSelectAvatarImageClicked(view: View) {
        (view.tag as? String)?.toInt()?.let { selectImage(it) }
    }

    private fun updateDrawer(destination: NavDestination) {
        drawer_layout.setDrawerLockMode(
            if (destination.id in TOP_LEVEL_DESTINATIONS)
                DrawerLayout.LOCK_MODE_UNLOCKED
            else
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        )
    }

    private fun setTitleInfo(info: MainActivityViewModel.PostInfo?) {
        if (currentFragmentAs<ToolbarUsingFragment>() == null) {
            return
        }

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
        toolbar.setTitleTextAppearance(
            this,
            R.style.AppTheme_TextAppearance_ActionBar_Title_Condensed
        )

        val size = resources.getDimensionPixelOffset(R.dimen.toolbar_logo_picture_size)

        if (info.author != null) {
            AppGlide.with(this)
                .load(info.author.avatar ?: R.drawable.default_avatar)
                .placeholder(android.R.color.transparent)
                .transform(
                    CenterCrop(),
                    RoundedCorners(resources.getDimensionPixelOffset(R.dimen.toolbar_logo_picture_rounding))
                )
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(object : CustomTarget<Drawable>(size, size) {
                    override fun onLoadCleared(placeholder: Drawable?) = Unit

                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        toolbar.logo = resource
                        toolbar.children
                            .filterIsInstance<ImageView>()
                            .find { it.drawable == resource }
                            ?.setOnClickListener {
                                findNavController(R.id.navigation_host)
                                    .navigate(
                                        NavigationMainDirections.actionGlobalFragmentUser(
                                            author = info.author
                                        )
                                    )
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
            .setView(R.layout.main_profile_editor)
            .setNegativeButton(R.string.cancel) { _: DialogInterface, _: Int ->
                viewModel.newUserAvatar.removeObservers(this)
                viewModel.resetPendingProfileAvatar()
            }
            .setPositiveButton(R.string.ok) { _: DialogInterface, _: Int ->
                viewModel.newUserAvatar.removeObservers(this)
                launch {
                    val bio = dialog.findViewById<TextView>(R.id.user_bio)?.text ?: ""
                    viewModel.sendProfile(bio.toString())
                }
            }
            .create()
            .apply { show() }

        val avatar = dialog.findViewById<ImageView>(R.id.user_picture)!!
        val avatarTransform = MultiTransformation(
            CenterCrop(),
            RoundedCorners(resources.getDimensionPixelOffset(R.dimen.dialog_user_picture_rounding))
        )
        val imageTransition = DrawableTransitionOptions.withCrossFade()

        AppGlide.with(this)
            .load(viewModel.userAvatar.value)
            .transition(imageTransition)
            .transform(avatarTransform)
            .into(avatar)

        viewModel.newUserAvatar.observe(this) {
            it?.run {
                AppGlide.with(this@MainActivity)
                    .load(Drawable.createFromStream(ByteArrayInputStream(bytes), "avatar"))
                    .transform(avatarTransform)
                    .transition(imageTransition)
                    .into(avatar)
            }
        }

        viewModel.userBio.value?.let {
            dialog.findViewById<EditText>(R.id.user_bio)?.run {
                setText(it)

                if (it.isNotEmpty()) {
                    minLines = 0
                }
            }
        }
    }

    private inline fun <reified T> currentFragmentAs(): T? {
        val destinationFragments = supportFragmentManager.fragments
            .firstOrNull { it is NavHostFragment }
            ?.childFragmentManager
            ?.fragments
            ?.filterIsInstance<FailureHandlingFragment>()

        destinationFragments?.let {
            val last = it.last()

            if (it.isNotEmpty() && last as? T != null) {
                return last
            }
        } ?: return null

        return null
    }

    private companion object {
        val TOP_LEVEL_DESTINATIONS = setOf(
            R.id.fragment_home,
            R.id.fragment_notifications,
            R.id.fragment_archive,
            R.id.fragment_own_posts,
            R.id.fragment_drafts
        )
        val NO_TITLE_DESTINATIONS = setOf(
            R.id.fragment_home,
            R.id.fragment_post,
            R.id.fragment_user,
            R.id.fragment_draft
        )

        val POST_REGEX = Regex("/areas/(\\w+)/(\\d+)(?:/(\\d+))?")
        val USER_REGEX = Regex("/user/(\\d+)")
        val REGEX_TO_DIRECTIONS = mapOf(
            POST_REGEX to { result: MatchResult ->
                NavigationMainDirections.actionGlobalFragmentPost(
                    areaName = result.groupValues[1],
                    postId = result.groupValues[2].toLong(),
                    selectedCommentId = result.groupValues[3]
                        .takeIf { it.isNotEmpty() }
                        ?.toLong()
                        ?: -1
                )
            },
            USER_REGEX to { result: MatchResult ->
                NavigationMainDirections.actionGlobalFragmentUser(
                    userId = result.groupValues[1].toLong()
                )
            }
        )
    }

    private class OnToolbarChangeListener(val toolbar: Toolbar) : View.OnLayoutChangeListener {
        override fun onLayoutChange(
            v: View,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int,
            oldLeft: Int,
            oldTop: Int,
            oldRight: Int,
            oldBottom: Int
        ) {
            if (toolbar.isTitleTruncated && toolbar.subtitle.isNullOrEmpty()) {
                toolbar.title = ""
            }
        }
    }
}
