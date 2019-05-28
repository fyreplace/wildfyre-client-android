package net.wildfyre.client.views

import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.core.view.doOnLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_app_bar.*
import net.wildfyre.client.AppGlide
import net.wildfyre.client.NavigationMainDirections
import net.wildfyre.client.R
import net.wildfyre.client.databinding.*
import net.wildfyre.client.viewmodels.MainActivityViewModel
import net.wildfyre.client.viewmodels.lazyViewModel
import java.io.ByteArrayInputStream

/**
 * The central activity that hosts the different fragments.
 */
class MainActivity : FailureHandlingActivity(), NavController.OnDestinationChangedListener,
    DrawerLayout.DrawerListener {
    override val viewModel by lazyViewModel<MainActivityViewModel>()
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        MainNavActionsThemeBinding.bind(navigation_view.menu.findItem(R.id.theme_selector).actionView)
            .run { lifecycleOwner = this@MainActivity; model = viewModel }
        MainNavActionsBadgeBinding.bind(navigation_view.menu.findItem(R.id.badge_toggle).actionView)
            .run { lifecycleOwner = this@MainActivity; model = viewModel }
        MainAppBarBinding.bind(content)
            .run { lifecycleOwner = this@MainActivity; model = viewModel }

        viewModel.authToken.observe(this, Observer {
            if (it.isNotEmpty()) {
                viewModel.updateInterfaceInformation()
            } else {
                val navController = findNavController(R.id.navigation_host)

                if (navController.currentDestination?.id !in LOGIN_DESTINATIONS) {
                    navController.navigate(
                        if (viewModel.startupLogin)
                            NavigationMainDirections.actionGlobalFragmentLoginStartup()
                        else
                            NavigationMainDirections.actionGlobalFragmentLogin()
                    )
                }
            }
        })

        viewModel.selectedThemeIndex.observe(this, Observer {
            AppCompatDelegate.setDefaultNightMode(MainActivityViewModel.THEMES[it])
            delegate.applyDayNight()
        })

        viewModel.userAvatar.observe(this, Observer {
            AppGlide.with(this)
                .load(it)
                .transform(
                    CenterCrop(),
                    RoundedCorners(resources.getDimensionPixelOffset(R.dimen.nav_header_user_picture_rounding))
                )
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(navHeaderBinding.userPicture)
        })

        setSupportActionBar(toolbar)
        drawer_layout.addDrawerListener(this)

        val hostFragment = supportFragmentManager.findFragmentById(R.id.navigation_host) as NavHostFragment
        appBarConfiguration = AppBarConfiguration(TOP_LEVEL_DESTINATIONS, drawer_layout)

        setupActionBarWithNavController(hostFragment.navController, appBarConfiguration)
        navigation_view.setupWithNavController(hostFragment.navController)
        hostFragment.navController.addOnDestinationChangedListener(this)

        MainActivityViewModel.NAVIGATION_LINKS.forEach { pair ->
            navigation_view.menu.findItem(pair.key).setOnMenuItemClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(pair.value)))
                true
            }
        }

        navigation_view.menu.findItem(R.id.logout).setOnMenuItemClickListener {
            viewModel.logout()
            true
        }

        navigation_view.doOnLayout {
            navigation_view.findViewById<View>(R.id.edit)?.setOnClickListener { editProfile() }
        }

        toolbar.doOnLayout {
            val layoutParams = badge.layoutParams as ViewGroup.MarginLayoutParams?
            layoutParams?.setMargins(
                it.height / 2,
                it.height / 2 - resources.getDimensionPixelOffset(R.dimen.margin_vertical_medium),
                0,
                0
            )
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent == null) {
            return
        }

        POST_REGEX.matchEntire(intent.data?.path ?: "")?.let { result ->
            findNavController(R.id.navigation_host).navigate(
                NavigationMainDirections.actionGlobalFragmentPost(
                    result.groupValues[1],
                    result.groupValues[2].toLong(),
                    result.groupValues[3].takeIf { it.isNotEmpty() }?.toLong() ?: -1
                )
            )
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
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK || data == null) {
            return
        }

        if (requestCode == REQUEST_AVATAR) {
            lateinit var mimeType: String

            contentResolver.query(
                data.data!!,
                arrayOf(MediaStore.MediaColumns.MIME_TYPE),
                null,
                null,
                null
            ).use {
                if (it!!.moveToFirst()) {
                    mimeType = it.getString(it.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
                } else {
                    return
                }
            }

            contentResolver.openInputStream(data.data!!).use {
                val bytes = it!!.readBytes()

                if (bytes.size < MAX_AVATAR_IMAGE_SIZE) {
                    val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
                    viewModel.setPendingProfileAvatar("avatar.$extension", mimeType, bytes)
                } else {
                    Toast.makeText(this, R.string.failure_avatar_size, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        drawer_layout.setDrawerLockMode(
            if (destination.id in TOP_LEVEL_DESTINATIONS)
                DrawerLayout.LOCK_MODE_UNLOCKED
            else
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        )

        when {
            destination.id in LOGIN_DESTINATIONS -> toolbar.navigationIcon = null
            toolbar.navigationIcon == null -> toolbar.navigationIcon =
                DrawerArrowDrawable(this).apply { isSpinEnabled = true }
        }

        viewModel.setNotificationBadgeVisible(
            destination.id != R.id.fragment_notifications
                && destination.id in TOP_LEVEL_DESTINATIONS
        )

        if (destination.id in NO_TITLE_DESTINATIONS) {
            toolbar.title = ""
        }
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) = Unit

    override fun onDrawerOpened(drawerView: View) = viewModel.updateNotificationCount()

    override fun onDrawerClosed(drawerView: View) = Unit

    override fun onDrawerStateChanged(newState: Int) = Unit

    /**
     * Shows a dialog to let the user edit their profile bio and their avatar.
     */
    private fun editProfile() {
        lateinit var dialog: AlertDialog
        lateinit var avatarDataObserver: Observer<ByteArray>

        dialog = AlertDialog.Builder(this)
            .setView(R.layout.profile_editor)
            .setNegativeButton(android.R.string.cancel) { _: DialogInterface, _: Int ->
                viewModel.userAvatarNewData.removeObserver(avatarDataObserver)
                viewModel.resetPendingProfileAvatar()
            }
            .setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int ->
                viewModel.userAvatarNewData.removeObserver(avatarDataObserver)
                viewModel.setProfile(dialog.findViewById<TextView>(R.id.user_bio)!!.text.toString())
                viewModel.resetPendingProfileAvatar()
            }
            .create()
            .apply { show() }

        val avatar = dialog.findViewById<ImageView>(R.id.user_picture)!!
        val transformations = MultiTransformation(
            CenterCrop(),
            RoundedCorners(resources.getDimensionPixelOffset(R.dimen.dialog_user_picture_rounding))
        )
        val transition = DrawableTransitionOptions.withCrossFade()

        AppGlide.with(this)
            .load(viewModel.userAvatar.value)
            .transform(transformations)
            .transition(transition)
            .into(avatar)

        avatarDataObserver = Observer {
            it?.run {
                val input = ByteArrayInputStream(this)
                AppGlide.with(this@MainActivity)
                    .load(Drawable.createFromStream(input, "avatar"))
                    .transform(transformations)
                    .transition(transition)
                    .into(avatar)
            }
        }

        viewModel.userAvatarNewData.observe(this, avatarDataObserver)

        dialog.findViewById<View>(R.id.user_picture_change)!!.setOnClickListener {
            startActivityForResult(
                Intent.createChooser(
                    Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" },
                    getString(R.string.main_profile_editor_avatar_chooser)
                ),
                REQUEST_AVATAR
            )
        }

        viewModel.userBio.value?.let {
            val bioEdit = dialog.findViewById<EditText>(R.id.user_bio)!!
            bioEdit.setText(it)

            if (it.isNotEmpty()) {
                bioEdit.minLines = 0
            }
        }
    }

    private companion object {
        const val REQUEST_AVATAR = 0
        const val MAX_AVATAR_IMAGE_SIZE = 512 * 1024

        val TOP_LEVEL_DESTINATIONS = setOf(
            R.id.fragment_home,
            R.id.fragment_notifications,
            R.id.fragment_archive,
            R.id.fragment_own_posts
        )
        val LOGIN_DESTINATIONS = setOf(
            R.id.fragment_login,
            R.id.action_global_fragment_login,
            R.id.action_global_fragment_login_startup
        )
        val NO_TITLE_DESTINATIONS = setOf(
            R.id.fragment_home,
            R.id.fragment_post
        )

        val POST_REGEX = Regex("/areas/(\\w+)/(\\d+)(?:/(\\d+))?")
    }
}
