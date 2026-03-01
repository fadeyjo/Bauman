package com.example.data_provider_app.ui.Main.Fragments.Profile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.data_provider_app.BuildConfig
import com.example.data_provider_app.R
import com.example.data_provider_app.glide.GlideApp
import com.example.data_provider_app.ui.Main.MainViewModel
import com.example.data_provider_app.ui.Main.UpdateProfileInfoState
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.getValue

class EditProfileFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var etEmail: EditText
    private lateinit var tilEmail: TextInputLayout
    private lateinit var etPhone: EditText
    private lateinit var tilPhone: TextInputLayout
    private lateinit var etLastName: EditText
    private lateinit var tilLastName: TextInputLayout
    private lateinit var etFirstName: EditText
    private lateinit var tilFirstName: TextInputLayout
    private lateinit var etPatronymic: EditText
    private lateinit var tilPatronymic: TextInputLayout
    private lateinit var etBirth: EditText
    private lateinit var tilBirth: TextInputLayout
    private lateinit var etDriveLicense: EditText
    private lateinit var tilDriveLicense: TextInputLayout
    private lateinit var btnUpdateInfo: Button

    private lateinit var ivAvatar: ImageView

    private var selectedAvatarFile: File? = null
    private var selectedMimeType: String? = null

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                handleSelectedImage(it)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity)
            .supportActionBar
            ?.title = "Редактирование профиля"
    }

    @SuppressLint("DefaultLocale")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        etEmail = view.findViewById(R.id.etEmail)
        tilEmail = view.findViewById(R.id.tilEmail)
        etPhone = view.findViewById(R.id.etPhone)
        tilPhone = view.findViewById(R.id.tilPhone)
        etLastName = view.findViewById(R.id.etLastName)
        tilLastName = view.findViewById(R.id.tilLastName)
        etFirstName = view.findViewById(R.id.etFirstName)
        tilFirstName = view.findViewById(R.id.tilFirstName)
        etPatronymic = view.findViewById(R.id.etPatronymic)
        tilPatronymic = view.findViewById(R.id.tilPatronymic)
        etBirth = view.findViewById(R.id.etBirth)
        tilBirth = view.findViewById(R.id.tilBirth)
        etDriveLicense = view.findViewById(R.id.etDriveLicense)
        tilDriveLicense = view.findViewById(R.id.tilDriveLicense)
        btnUpdateInfo = view.findViewById(R.id.btnUpdateInfo)
        ivAvatar = view.findViewById(R.id.ivAvatar)

        btnUpdateInfo.setOnClickListener { updateUser() }

        etBirth.setOnClickListener {
            val today = Calendar.getInstance()
            today.add(Calendar.YEAR, -18)
            today.add(Calendar.DAY_OF_MONTH, -1)

            val year = today.get(Calendar.YEAR)
            val month = today.get(Calendar.MONTH)
            val day = today.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = String.format("%02d.%02d.%d", selectedDay, selectedMonth + 1, selectedYear)
                    etBirth.setText(formattedDate)
                }, year, month, day
            )

            datePicker.datePicker.maxDate = today.timeInMillis

            datePicker.show()
        }

        ivAvatar.setOnClickListener {
            openGallery()
        }

        observerViewModel()

        initForm()

        return view
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun handleSelectedImage(uri: Uri) {

        val contentResolver = requireContext().contentResolver
        val mimeType = contentResolver.getType(uri) ?: "image/*"

        selectedMimeType = mimeType

        val extension = android.webkit.MimeTypeMap
            .getSingleton()
            .getExtensionFromMimeType(mimeType) ?: "png"

        val file = File(requireContext().cacheDir, "avatar.$extension")

        contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        selectedAvatarFile = file

        ivAvatar.setImageURI(uri)
    }

    private fun initForm() {
        if (viewModel.person == null)
            return

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val formattedDate = viewModel.person!!.birth.format(formatter)

        etEmail.setText(viewModel.person!!.email)
        etPhone.setText(viewModel.person!!.phone)
        etLastName.setText(viewModel.person!!.lastName)
        etFirstName.setText(viewModel.person!!.firstName)
        viewModel.person!!.patronymic?.let { etPatronymic.setText(viewModel.person!!.patronymic) }
        etBirth.setText(formattedDate)
        etDriveLicense.setText(viewModel.person!!.driveLicense)

        val imageUrl = BuildConfig.BASE_URL + "api/avatars/avatar_id/${viewModel.person!!.avatarId}"

        GlideApp.with(ivAvatar)
            .load(imageUrl)
            .placeholder(R.drawable.loading)
            .error(R.drawable.no_image)
            .circleCrop()
            .into(ivAvatar)
    }

    private fun observerViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.updateProfileInfoState.collect { state ->

                        when (state) {

                            is UpdateProfileInfoState.Loading -> {
                                btnUpdateInfo.isEnabled = false
                            }

                            is UpdateProfileInfoState.Updated -> {
                                viewModel.getUserInfo()

                                viewModel.resetUpdateProfileInfoState()

                                parentFragmentManager.popBackStack()
                            }

                            is UpdateProfileInfoState.Error -> {
                                btnUpdateInfo.isEnabled = true
                                showErrorDialog(state.message)
                            }

                            is UpdateProfileInfoState.NetworkError -> {
                                btnUpdateInfo.isEnabled = true
                                showErrorDialog("Нет подключения к интернету")
                            }

                            is UpdateProfileInfoState.ValidationError -> {
                                btnUpdateInfo.isEnabled = true
                                state.map.forEach { (field, errorMessage) ->
                                    when (field) {
                                        "Email" -> tilEmail.error = errorMessage
                                        "Phone" -> tilPhone.error = errorMessage
                                        "LastName" -> tilLastName.error = errorMessage
                                        "FirstName" -> tilFirstName.error = errorMessage
                                        "Patronymic" -> tilPatronymic.error = errorMessage
                                        "Birth" -> tilBirth.error = errorMessage
                                        "DriveLicense" -> tilDriveLicense.error = errorMessage
                                    }
                                }
                            }

                            UpdateProfileInfoState.UnknownError -> {
                                btnUpdateInfo.isEnabled = true
                                showErrorDialog("Неизвестная ошибка")
                            }

                            UpdateProfileInfoState.Idle -> {}
                        }
                    }
                }
            }
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Ошибка")
            .setMessage(message)
            .setPositiveButton("ОК", null)
            .show()
    }

    private fun updateUser() {
        btnUpdateInfo.isEnabled = false

        viewModel.resetUpdateProfileInfoState()

        val email = etEmail.text.toString()
        val phone = etPhone.text.toString()
        val lastName = etLastName.text.toString()
        val firstName = etFirstName.text.toString()
        val patronymic = etPatronymic.text.toString()
        val birth = etBirth.text.toString()
        val driveLicense = etDriveLicense.text.toString()

        var isValid = true

        if (email.isBlank()) {
            tilEmail.error = "Введите email"
            isValid = false
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Некорректный формат email"
            isValid = false
        }
        else {
            tilEmail.error = null
        }

        val phoneRegex1 = Regex("""^\+7\d*$""")
        val phoneRegex2 = Regex("""^\+7\d{10}$""")
        if (phone.isBlank()) {
            tilPhone.error = "Введите номер телефона"
            isValid = false
        }
        else if (!phoneRegex1.matches(phone)) {
            tilPhone.error = "Номер телефона должен состоять из +7 и цифр"
            isValid = false
        }
        else if (!phoneRegex2.matches(phone)) {
            tilPhone.error = "Номер телефона должен содержать 11 цифр"
            isValid = false
        }
        else {
            tilPhone.error = null
        }

        val namesRegex1 = Regex("""^[А-ЯЁа-яё]*$""")
        val namesRegex2 = Regex("""^[А-ЯЁ][а-яё]*$""")
        val namesRegex3 = Regex("""^[А-ЯЁ][а-яё]{1,49}$""")

        if (lastName.isBlank()) {
            tilLastName.error = "Введите фамилию"
            isValid = false
        }
        else if (!namesRegex1.matches(lastName)) {
            tilLastName.error = "Фамилия должна состоять только из букв"
            isValid = false
        }
        else if (!namesRegex2.matches(lastName)) {
            tilLastName.error = "Первая буква - строчная, остальные - прописные"
            isValid = false
        }
        else if (!namesRegex3.matches(lastName)) {
            tilLastName.error = "Минимальная длина 2, максимальная - 50"
            isValid = false
        }
        else {
            tilLastName.error = null
        }

        if (firstName.isBlank()) {
            tilFirstName.error = "Введите имя"
            isValid = false
        }
        else if (!namesRegex1.matches(firstName)) {
            tilFirstName.error = "Имя должно состоять только из букв"
            isValid = false
        }
        else if (!namesRegex2.matches(firstName)) {
            tilFirstName.error = "Первая буква - строчная, остальные - прописные"
            isValid = false
        }
        else if (!namesRegex3.matches(firstName)) {
            tilFirstName.error = "Минимальная длина 2, максимальная - 50"
            isValid = false
        }
        else {
            tilFirstName.error = null
        }

        if (!patronymic.isBlank()) {
            if (!namesRegex1.matches(patronymic)) {
                tilPatronymic.error = "Отчество должно состоять только из букв"
                isValid = false
            }
            else if (!namesRegex2.matches(patronymic)) {
                tilPatronymic.error = "Первая буква - строчная, остальные - прописные"
                isValid = false
            }
            else if (!namesRegex3.matches(patronymic)) {
                tilPatronymic.error = "Минимальная длина 2, максимальная - 50"
                isValid = false
            }
            else {
                tilPatronymic.error = null
            }
        }
        else {
            tilPatronymic.error = null
        }

        if (birth.isBlank()) {
            tilBirth.error = "Введите дату рождения"
            isValid = false
        }
        else {
            tilBirth.error = null
        }

        val driveLicenseRegex1 = Regex("""^\d*$""")
        val driveLicenseRegex2 = Regex("""^\d{10}$""")
        if (birth.isBlank()) {
            tilDriveLicense.error = "Введите дату рождения"
            isValid = false
        }
        else if (!driveLicenseRegex1.matches(driveLicense)) {
            tilDriveLicense.error = "Номер ВУ должен состоять только из цифр"
            isValid = false
        }
        else if (!driveLicenseRegex2.matches(driveLicense)) {
            tilDriveLicense.error = "Номер ВУ должен состоять только из 10 цифр"
            isValid = false
        }
        else {
            tilDriveLicense.error = null
        }


        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val birthDate = LocalDate.parse(birth, formatter)

        if (isValid) {
            viewModel.updateProfileInfo(
                email, phone,
                lastName, firstName,
                patronymic.ifBlank { null }, birthDate,
                driveLicense, selectedAvatarFile, selectedMimeType ?: "image/*"
            )
        }
        else {
            btnUpdateInfo.isEnabled = true
        }
    }
}