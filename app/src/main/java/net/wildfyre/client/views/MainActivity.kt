package net.wildfyre.client.views

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.core.view.doOnLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.transaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_app_bar.*
import kotlinx.android.synthetic.main.main_nav_header.*
import net.wildfyre.client.AppGlide
import net.wildfyre.client.Constants
import net.wildfyre.client.R
import net.wildfyre.client.databinding.MainNavHeaderBinding
import net.wildfyre.client.viewmodels.MainActivityViewModel

class MainActivity : FailureHandlingActivity(), NavigationView.OnNavigationItemSelectedListener {
    override lateinit var viewModel: MainActivityViewModel
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        val startingNightMode = getPreferences(Context.MODE_PRIVATE).getInt(
            Constants.Preferences.UI_THEME,
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        )

        AppCompatDelegate.setDefaultNightMode(startingNightMode)
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
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

        val binding = MainNavHeaderBinding.bind(navigation_drawer.getHeaderView(0))
        binding.lifecycleOwner = this
        binding.model = viewModel

        setSupportActionBar(toolbar)
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.main_drawer_open,
            R.string.main_drawer_close
        )

        val themeSelector = navigation_drawer.menu.findItem(R.id.theme_selector).actionView as Spinner?
        themeSelector?.run {
            adapter = ArrayAdapter<String>(
                this@MainActivity,
                android.R.layout.simple_spinner_dropdown_item,
                viewModel.themes.map { pair -> getString(pair.first) })
            setSelection(viewModel.themes.indexOf(viewModel.themes.find { pair -> pair.second == startingNightMode }))
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val theme = viewModel.themes[position].second
                    getPreferences(Context.MODE_PRIVATE).edit { putInt(Constants.Preferences.UI_THEME, theme) }
                    AppCompatDelegate.setDefaultNightMode(theme)
                    delegate.applyDayNight()
                }
            }
        }

        viewModel.userAvatar.observe(this, Observer {
            AppGlide.with(this).load(it).into(binding.userPicture)
        })
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawer_layout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navigation_drawer.setNavigationItemSelectedListener(this)
        navigation_drawer.doOnLayout { edit.setOnClickListener { editProfile() } }
        tryNavigateTo(
            savedInstanceState?.getInt(
                Constants.Save.ACTIVITY_NAVIGATION,
                R.id.fragment_home
            ) ?: R.id.fragment_home
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        navigation_drawer.checkedItem?.run { outState.putInt(Constants.Save.ACTIVITY_NAVIGATION, itemId) }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        actionBarDrawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val isLoginStep = supportFragmentManager.fragments.count { it is LoginFragment } > 0
        drawer_layout.setDrawerLockMode(if (isLoginStep) DrawerLayout.LOCK_MODE_LOCKED_CLOSED else DrawerLayout.LOCK_MODE_UNLOCKED)
        actionBarDrawerToggle.isDrawerIndicatorEnabled = !isLoginStep
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return actionBarDrawerToggle.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when {
            viewModel.navigationLinks.containsKey(item.itemId) -> startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(viewModel.navigationLinks[item.itemId])
                )
            )
            item.isChecked -> return false
        }

        var closeDrawer = true
        val newFragment = when (item.itemId) {
            R.id.fragment_login,
            R.id.logout -> LoginFragment().also {
                it.setOnReceivedTokenListener { navigateTo(R.id.fragment_home) }

                if (item.itemId == R.id.logout) {
                    viewModel.clearAuthToken()
                    navigation_drawer.setCheckedItem(R.id.fragment_login)
                }
            }
            R.id.fragment_home -> HomeFragment()
            R.id.fragment_notifications -> NotificationsFragment()
            R.id.fragment_posts -> PostsFragment()
            else -> {
                closeDrawer = false
                null
            }
        }

        newFragment?.let {
            supportFragmentManager.transaction(true, true) {
                replace(
                    R.id.fragment_container,
                    it,
                    getString(
                        when (item.itemId) {
                            R.id.fragment_login -> R.string.main_nav_login
                            R.id.fragment_home -> R.string.main_nav_home
                            R.id.fragment_notifications -> R.string.main_nav_notifications
                            R.id.fragment_posts -> R.string.main_nav_posts
                            else -> R.string.app_name
                        }
                    )
                )
            }
        }

        if (closeDrawer) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }

        return closeDrawer
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun tryNavigateTo(@IdRes id: Int) {
        if (supportFragmentManager.fragments.count() > 0) {
            return
        }

        val token = viewModel.authToken.value ?: ""
        navigateTo(if (token.isEmpty()) R.id.fragment_login else id)
    }

    private fun navigateTo(@IdRes id: Int) {
        onNavigationItemSelected(navigation_drawer.menu.findItem(id))
        navigation_drawer.setCheckedItem(id)
    }

    private fun editProfile() {
    }
}