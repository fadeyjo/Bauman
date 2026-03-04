package com.example.data_provider_app.ui.Main.Fragments.MyCars

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.data_provider_app.BuildConfig
import com.example.data_provider_app.R
import com.example.data_provider_app.databinding.FragmentAddCarBinding
import com.example.data_provider_app.glide.GlideApp
import com.example.data_provider_app.ui.Main.AddCarState
import com.example.data_provider_app.ui.Main.DirectoryState
import com.example.data_provider_app.ui.Main.MainViewModel
import com.example.data_provider_app.ui.Main.UserViewState
import kotlinx.coroutines.launch
import java.time.Year
import kotlin.getValue
import kotlin.math.roundToInt

class AddCarFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentAddCarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddCarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity)
            .supportActionBar
            ?.title = "Добавление автомобиля"

        binding.etBrand.setOnClickListener { selectBrand() }

        binding.etModel.setOnClickListener { selectModel() }

        binding.btnAddCar.setOnClickListener { addCar() }

        parentFragmentManager.setFragmentResultListener(
            "brand_request",
            viewLifecycleOwner
        ) { _, bundle ->
            val selectedBrand = bundle.getString("brand")
            binding.etBrand.error = null
            binding.etBrand.setText(selectedBrand)
        }

        parentFragmentManager.setFragmentResultListener(
            "model_request",
            viewLifecycleOwner
        ) { _, bundle ->
            val selectedModel = bundle.getString("model")
            binding.etModel.setText(selectedModel)
        }

        observeViewModel()

        viewModel.getAllFuelTypes()
        viewModel.getAllBodyTypes()
        viewModel.getAllGearboxTypes()
        viewModel.getAllDriveTypes()
    }

    private fun addCar() {
        binding.btnAddCar.isEnabled = false

        var isValid = true

        val brand = binding.etBrand.text.toString()
        val model = binding.etModel.text.toString()
        val body = binding.actvBody.text.toString()
        val year = binding.etYear.text.toString()
        val gearbox = binding.actvGearbox.text.toString()
        val drive = binding.actvDrive.text.toString()
        val weight = binding.etWeight.text.toString()
        val powerHp = binding.etPowerHp.text.toString()
        val powerKw = binding.etPowerKw.text.toString()
        val engVolume = binding.etEngineVolume.text.toString()
        val tankVolume = binding.etTankVolume.text.toString()
        val fuelType = binding.actvFuel.text.toString()
        val VIN = binding.etVIN.text.toString()
        val stateNumber = binding.etStateNumber.text.toString()

        if (brand.isBlank()) {
            binding.tilBrand.error = "Введите марку"
            isValid = false
        }
        else {
            binding.tilBrand.error = null
        }

        if (model.isBlank()) {
            binding.tilModel.error = "Введите модель"
            isValid = false
        }
        else {
            binding.tilModel.error = null
        }

        if (body.isBlank()) {
            binding.tilBody.error = "Введите кузов"
            isValid = false
        }
        else {
            binding.tilBody.error = null
        }

        if (year.isBlank()) {
            binding.tilYear.error = "Введите год выпуска"
            isValid = false
        }
        else if (year.toIntOrNull() == null) {
            binding.tilYear.error = "Год - целое число"
            isValid = false
        }
        else if (year.toInt() < 2000) {
            binding.tilYear.error = "Только автомобили с 2000 г.в."
            isValid = false
        }
        else if (year.toInt() > Year.now().value) {
            binding.tilYear.error = "Некорректный год выпуска"
            isValid = false
        }
        else {
            binding.tilYear.error = null
        }

        if (gearbox.isBlank()) {
            binding.tilGearbox.error = "Введите КПП"
            isValid = false
        }
        else {
            binding.tilGearbox.error = null
        }

        if (drive.isBlank()) {
            binding.tilDrive.error = "Введите привод"
            isValid = false
        }
        else {
            binding.tilDrive.error = null
        }

        if (weight.isBlank()) {
            binding.tilWeight.error = "Введите массу автомобиля"
            isValid = false
        }
        else if (weight.toIntOrNull() == null) {
            binding.tilWeight.error = "Масса - целое число"
            isValid = false
        }
        else if (weight.toInt() < 500) {
            binding.tilWeight.error = "Минимальная масса - 500 кг"
            isValid = false
        }
        else {
            binding.tilWeight.error = null
        }

        if (powerHp.isBlank()) {
            binding.tilPowerHp.error = "Введите мощность (л.с.)"
            isValid = false
        }
        else if (powerHp.toIntOrNull() == null) {
            binding.tilPowerHp.error = "Мощность (л.с.) - целое число"
            isValid = false
        }
        else {
            binding.tilPowerHp.error = null
        }

        if (powerKw.isBlank()) {
            binding.tilPowerKw.error = "Введите мощность (кВт)"
            isValid = false
        }
        else {
            binding.tilPowerKw.error = null
        }

        if (engVolume.isBlank()) {
            binding.tilEngineVolume.error = "Введите объём двигателя"
            isValid = false
        }
        else {
            binding.tilEngineVolume.error = null
        }

        if (tankVolume.isBlank()) {
            binding.tilTankVolume.error = "Введите объём бака"
            isValid = false
        }
        else if (tankVolume.toIntOrNull() == null) {
            binding.tilTankVolume.error = "Объём бака - целое число"
            isValid = false
        }
        else {
            binding.tilTankVolume.error = null
        }

        if (fuelType.isBlank()) {
            binding.tilFuel.error = "Введите топливо"
            isValid = false
        }
        else {
            binding.tilFuel.error = null
        }

        if (VIN.isBlank()) {
            binding.tilVIN.error = "Введите VIN"
            isValid = false
        }
        else if (VIN.length != 17) {
            binding.tilVIN.error = "Длина VIN - 17 символов"
            isValid = false
        }
        else {
            binding.tilVIN.error = null
        }

        if (!stateNumber.isBlank()) {
            val pattern = Regex("^[авекмнорстухАВЕКМНОРСТУХ][0-9]{3}[авекмнорстухАВЕКМНОРСТУХ]{2}[0-9]{2,3}$")


            if (!pattern.matches(stateNumber)) {
                binding.tilStateNumber.error = "Некорректный формат"
            }
            else {
                binding.tilStateNumber.error = null
            }
        }
        else {
            binding.tilStateNumber.error = null
        }

        if (isValid)
            viewModel.addCar(
                VIN, if (stateNumber.length == 0) null else stateNumber,
                brand, model,
                body, year.toUShort(),
                gearbox, drive,
                weight.toUShort(), powerHp.toUShort(),
                (powerKw.toFloat() * 10).roundToInt() / 10f, (engVolume.toFloat() * 10).roundToInt() / 10f,
                tankVolume.toUByte(), fuelType

            )
    }

    private fun selectBrand() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, SelectBrandFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun selectModel() {
        val brand = binding.etBrand.text.toString()

        if (brand.isEmpty()) {
            binding.tilBrand.error = "Выберите марку"
            return
        }

        val fragment = SelectModelFragment().apply {
            arguments = Bundle().apply {
                putString("brand", brand)
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

                launch {
                    viewModel.fuelTypesState.collect { state ->
                        handleDirectoryState(state, "fuel")
                    }
                }

                launch {
                    viewModel.bodiesState.collect { state ->
                        handleDirectoryState(state, "body")
                    }
                }

                launch {
                    viewModel.gearboxesState.collect { state ->
                        handleDirectoryState(state, "gearbox")
                    }
                }

                launch {
                    viewModel.drivesState.collect { state ->
                        handleDirectoryState(state, "drive")
                    }
                }

                launch {
                    viewModel.addCarState.collect { state ->
                        when (state) {
                            is AddCarState.Added -> {
                                viewModel.getMyCars()

                                viewModel.resetMyCarsState()

                                parentFragmentManager.popBackStack()
                            }
                            is AddCarState.Error -> {
                                binding.btnAddCar.isEnabled = true
                                showShortToast(state.message)
                            }
                            is AddCarState.Idle -> {}
                            is AddCarState.Loading -> {}
                            is AddCarState.NetworkError -> {
                                binding.btnAddCar.isEnabled = true
                                showShortToast("Нет подключения к интернету")
                            }
                            is AddCarState.UnknownError -> {
                                binding.btnAddCar.isEnabled = true
                                showShortToast("Неизвестная ошибка")
                            }
                            is AddCarState.ValidationError -> {
                                state.map["vinNumber"]?.let {
                                    binding.tilVIN.error = state.map["vinNumber"]
                                }

                                state.map["StateNumber"]?.let {
                                    binding.tilStateNumber.error = state.map["StateNumber"]
                                }

                                state.map["BrandName"]?.let {
                                    binding.tilBrand.error = state.map["BrandName"]
                                }

                                state.map["ModelName"]?.let {
                                    binding.tilModel.error = state.map["ModelName"]
                                }

                                state.map["BodyName"]?.let {
                                    binding.tilBody.error = state.map["BodyName"]
                                }

                                state.map["ReleaseYear"]?.let {
                                    binding.tilYear.error = state.map["ReleaseYear"]
                                }

                                state.map["GearboxName"]?.let {
                                    binding.tilGearbox.error = state.map["GearboxName"]
                                }

                                state.map["DriveName"]?.let {
                                    binding.tilDrive.error = state.map["DriveName"]
                                }

                                state.map["VehicleWeightKG"]?.let {
                                    binding.tilWeight.error = state.map["VehicleWeightKG"]
                                }

                                state.map["EnginePowerHP"]?.let {
                                    binding.tilPowerHp.error = state.map["EnginePowerHP"]
                                }

                                state.map["EnginePowerKW"]?.let {
                                    binding.tilPowerKw.error = state.map["EnginePowerKW"]
                                }

                                state.map["EngineCapacityL"]?.let {
                                    binding.tilEngineVolume.error = state.map["EngineCapacityL"]
                                }

                                state.map["TankCapacityL"]?.let {
                                    binding.tilTankVolume.error = state.map["TankCapacityL"]
                                }

                                state.map["FuelTypeName"]?.let {
                                    binding.tilFuel.error = state.map["FuelTypeName"]
                                }

                                binding.btnAddCar.isEnabled = true
                            }

                            is AddCarState.StateNumberExists -> {
                                binding.tilStateNumber.error = "Уже существует автомобиль с данным гос. номером"

                                binding.btnAddCar.isEnabled = true
                            }
                            is AddCarState.VINExists -> {
                                binding.tilVIN.error = "Уже существует автомобиль с данным VIN"

                                binding.btnAddCar.isEnabled = true
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleDirectoryState(
        state: DirectoryState,
        type: String
    ) {
        when (state) {
            is DirectoryState.Data -> {
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    state.items
                )

                when (type) {
                    "fuel" -> binding.actvFuel.setAdapter(adapter)
                    "body" -> binding.actvBody.setAdapter(adapter)
                    "gearbox" -> binding.actvGearbox.setAdapter(adapter)
                    "drive" -> binding.actvDrive.setAdapter(adapter)
                }
            }

            is DirectoryState.Error -> showShortToast(state.message)
            is DirectoryState.NetworkError -> showShortToast("Нет подключения к интернету")
            is DirectoryState.UnknownError -> showShortToast("Неизвестная ошибка")
            is DirectoryState.ValidationError -> showShortToast("Ошибка валидации")
            is DirectoryState.Idle -> {}
            is DirectoryState.Loading -> {}
        }
    }

    private fun showShortToast(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}