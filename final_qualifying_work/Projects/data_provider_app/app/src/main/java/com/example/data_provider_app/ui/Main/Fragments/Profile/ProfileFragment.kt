package com.example.data_provider_app.ui.Main.Fragments.Profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.data_provider_app.R
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.data_provider_app.dto.PersonDto
import com.example.data_provider_app.ui.Main.MainViewModel
import com.example.data_provider_app.ui.Main.UserViewState
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

class ProfileFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var ivProfileAvatar: ImageView
    private lateinit var tvFullName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvBirthDate: TextView
    private lateinit var tvDriveLicense: TextView
    private lateinit var btnRedactPersonInfo: Button


    private lateinit var progressBar: View
    private lateinit var formContainer: View

    private var person: PersonDto? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        ivProfileAvatar = view.findViewById(R.id.ivProfileAvatar)
        tvFullName = view.findViewById(R.id.tvFullName)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvPhone = view.findViewById(R.id.tvPhone)
        tvBirthDate = view.findViewById(R.id.tvBirthDate)
        tvDriveLicense = view.findViewById(R.id.tvDriveLicense)
        btnRedactPersonInfo = view.findViewById(R.id.btnRedactPersonInfo)

        btnRedactPersonInfo.setOnClickListener { redactPersonInfo(person) }

        observeViewModel()

        parentFragmentManager.setFragmentResultListener(
            "edit_profile_result",
            viewLifecycleOwner
        ) { _, bundle ->

            val updated = bundle.getBoolean("updated", false)

            if (updated)
                viewModel.getUserInfo()
        }

        progressBar = view.findViewById(R.id.progressBar)
        formContainer = view.findViewById(R.id.formContainer)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity)
            .supportActionBar
            ?.title = "Профиль"
    }

    private fun redactPersonInfo(person: PersonDto?) {
        if (person == null) {
            showShortToast("Пользователь не определён")

            return
        }

        val fragment = EditProfileFragment().apply {
            arguments = Bundle().apply {
                putParcelable("person", person)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userState.collect { state ->
                    when (state) {
                        is UserViewState.Data -> {

                            person = state.person

                            progressBar.visibility = View.GONE

                            formContainer.visibility = View.VISIBLE

                            val fullName =
                                if (state.person.patronymic.isNullOrEmpty())
                                    "${state.person.lastName} ${state.person.firstName}"
                                else
                                    "${state.person.lastName} ${state.person.firstName} ${state.person.patronymic}"

                            tvFullName.text = fullName
                            ivProfileAvatar.setImageBitmap(state.bitmap)
                            tvEmail.text = state.person.email
                            tvPhone.text = formatPhone(state.person.phone)

                            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                            tvBirthDate.text = state.person.birth.format(formatter)

                            tvDriveLicense.text =
                                state.person.driveLisense?.let { formatDriveLicense(it) } ?: ""
                        }

                        is UserViewState.Error -> showShortToast(state.message)

                        is UserViewState.Idle -> {}
                        is UserViewState.Loading -> {}
                        is UserViewState.NetworkError -> showShortToast("Нет подключения к интернету")
                        is UserViewState.UnknownError -> showShortToast("Неизвестная ошибка")
                        is UserViewState.ValidationError -> showShortToast("Ошибка валидации")
                    }
                }
            }
        }
    }

    private fun formatPhone(phone: String): String {
        val cleaned = phone.replace(Regex("[^\\d+]"), "")

        val regex = Regex("^\\+(\\d)(\\d{3})(\\d{3})(\\d{2})(\\d{2})$")
        val match = regex.find(cleaned) ?: return phone

        val (country, part1, part2, part3, part4) = match.destructured
        return "+$country ($part1) $part2 $part3-$part4"
    }

    fun formatDriveLicense(driveLicense: String): String {
        val digits = driveLicense.filter { it.isDigit() }

        if (digits.length != 10) return driveLicense

        return digits.take(4) + " " + digits.drop(4)
    }

    private fun showShortToast(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}