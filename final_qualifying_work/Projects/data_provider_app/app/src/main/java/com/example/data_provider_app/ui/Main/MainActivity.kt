package com.example.data_provider_app.ui.Main

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.data_provider_app.R
import com.example.data_provider_app.ui.Main.Fragments.MyCarsFragment
import com.example.data_provider_app.ui.Main.Fragments.MyTripsFragment
import com.example.data_provider_app.ui.Main.Fragments.ProfileFragment
import com.example.data_provider_app.ui.Main.Fragments.StartTripFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open,
            R.string.close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            toolbar.title = "Профиль"
            replaceFragment(ProfileFragment())
            navigationView.setCheckedItem(R.id.profile)
        }

        navigationView.setNavigationItemSelectedListener { item ->

            when (item.itemId) {

                R.id.logout_all -> {
                    // TODO: твоя логика выхода
                    true
                }

                R.id.profile -> {
                    replaceFragment(ProfileFragment())
                    toolbar.title = "Профиль"
                    true
                }

                R.id.cars -> {
                    replaceFragment(MyCarsFragment())
                    toolbar.title = "Мои автомобили"
                    true
                }

                R.id.trips -> {
                    replaceFragment(MyTripsFragment())
                    toolbar.title = "Мои поездки"
                    true
                }

                R.id.startTrip -> {
                    replaceFragment(StartTripFragment())
                    toolbar.title = "Начать поездку"
                    true
                }

                else -> false
            }

        }

        navigationView.inflateMenu(R.menu.drawer_footer)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
