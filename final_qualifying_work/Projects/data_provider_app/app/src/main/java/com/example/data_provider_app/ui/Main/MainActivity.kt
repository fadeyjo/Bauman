package com.example.data_provider_app.ui.Main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.data_provider_app.BuildConfig
import com.example.data_provider_app.R
import com.example.data_provider_app.dto.PersonDto
import com.example.data_provider_app.glide.GlideApp
import com.example.data_provider_app.jwt.TokenStorage
import com.example.data_provider_app.ui.Main.Fragments.MyCars.MyCarsFragment
import com.example.data_provider_app.ui.Main.Fragments.MyTrips.MyTripsFragment
import com.example.data_provider_app.ui.Main.Fragments.Profile.ProfileFragment
import com.example.data_provider_app.ui.Main.Fragments.StartTrip.StartTripFragment
import com.example.data_provider_app.ui.SignIn.SignInActivity
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import kotlin.getValue

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var headerView: View
    private lateinit var ivAvatar: ImageView
    private lateinit var tvUserName: TextView

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        toolbar = findViewById(R.id.toolbar)
        headerView = navigationView.getHeaderView(0)
        ivAvatar = headerView.findViewById(R.id.ivAvatar)
        tvUserName = headerView.findViewById(R.id.tvUserName)

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

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.logout_all -> {
                    viewModel.logout()
                    true
                }

                R.id.profile -> {
                    replaceFragment(ProfileFragment())
                    supportActionBar?.title = "Профиль"
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                R.id.cars -> {
                    replaceFragment(MyCarsFragment())
                    supportActionBar?.title = "Мои автомобили"
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                R.id.trips -> {
                    replaceFragment(MyTripsFragment())
                    supportActionBar?.title = "Мои поездки"
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                R.id.startTrip -> {
                    replaceFragment(StartTripFragment())
                    supportActionBar?.title = "Начать поездку"
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                else -> false
            }
        }

        observeViewModel()

        viewModel.getUserInfo()

        if (supportFragmentManager.findFragmentById(R.id.fragmentContainer) == null) {
            supportActionBar?.title = "Профиль"
            replaceFragment(ProfileFragment())
            navigationView.setCheckedItem(R.id.profile)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.userState.collect { state ->
                when (state) {
                    is UserViewState.Data -> {
                        tvUserName.text =
                            if (state.person.patronymic.isNullOrEmpty())
                                "${state.person.lastName} ${state.person.firstName}"
                            else
                                "${state.person.lastName} ${state.person.firstName} ${state.person.patronymic}"

                        val imageUrl = BuildConfig.BASE_URL + "api/avatars/avatar_id/${state.person.avatarId}"

                        GlideApp.with(ivAvatar)
                            .load(imageUrl)
                            .placeholder(R.drawable.loading)
                            .error(R.drawable.no_image)
                            .circleCrop()
                            .into(ivAvatar)
                    }
                    is UserViewState.Error -> showErrorDialog(state.message)
                    is UserViewState.Idle -> {}
                    is UserViewState.Loading -> {}
                    is UserViewState.NetworkError -> showErrorDialog("Нет подключения к интернету")
                    is UserViewState.UnknownError -> showErrorDialog("Произошла неизвестная ошибка")
                    is UserViewState.ValidationError -> showErrorDialog("Ошибка валидации данных при запросе")
                }
            }
        }

        lifecycleScope.launch {
            viewModel.logoutState.collect { state ->
                when (state) {
                    is LogoutViewState.Logout -> {
                        TokenStorage.clear()
                        moveToSignIn()
                    }
                    is LogoutViewState.Error -> showErrorDialog(state.message)
                    is LogoutViewState.Idle -> {}
                    is LogoutViewState.Loading -> {}
                    is LogoutViewState.NetworkError -> showErrorDialog("Нет подключения к интернету")
                    is LogoutViewState.UnknownError -> showErrorDialog("Произошла неизвестная ошибка")
                    is LogoutViewState.ValidationError -> showErrorDialog("Ошибка валидации данных при запросе")
                }
            }
        }
    }

    private fun moveToSignIn() {
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Ошибка")
            .setMessage(message)
            .setPositiveButton("ОК", null)
            .show()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
