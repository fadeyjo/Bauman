package com.example.data_provider_app.ui.Main.Fragments.MyCars

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.data_provider_app.BuildConfig
import com.example.data_provider_app.R
import com.example.data_provider_app.databinding.FragmentCarDetailBinding
import com.example.data_provider_app.dto.CarDto
import com.example.data_provider_app.glide.GlideApp
import com.example.data_provider_app.ui.Main.GetCarState
import com.example.data_provider_app.ui.Main.MainViewModel
import kotlinx.coroutines.launch
import kotlin.getValue

class CarDetailFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    private var VIN: String = ""

    private var _binding: FragmentCarDetailBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_VIN = "VIN"

        fun newInstance(VIN: String): CarDetailFragment {
            val fragment = CarDetailFragment()
            val bundle = Bundle()
            bundle.putString(ARG_VIN, VIN)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        VIN = arguments?.getString(ARG_VIN) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()

        viewModel.getCarData(VIN)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getCarState.collect {state ->
                    when (state) {
                        is GetCarState.Data -> {
                            initForm(state.car)
                            binding.progressBar.visibility = View.GONE

                            binding.formContainer.visibility = View.VISIBLE
                        }
                        is GetCarState.Error -> showShortToast(state.message)
                        is GetCarState.Idle -> {}
                        is GetCarState.Loading -> {}
                        is GetCarState.NetworkError -> showShortToast("Нет подключения к интернету")
                        is GetCarState.UnknownError -> showShortToast("Неизвестная ошибка")
                        is GetCarState.ValidationError -> showShortToast("Ошибка валидации")
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initForm(car: CarDto) {
        val imageUrl = BuildConfig.BASE_URL + "api/carphotos/photo_id/${car.photoId}"
        GlideApp.with(binding.ivCarPhoto)
            .load(imageUrl)
            .placeholder(R.drawable.loading)
            .error(R.drawable.no_image)
            .into(binding.ivCarPhoto)

        binding.tvCarName.text = "${car.brandName} ${car.modelName}"

        binding.tvVIN.text = car.vinNumber
        binding.tvStateNumber.text = car.stateNumber ?: "-"

        binding.tvBody.text = car.bodyName
        binding.tvYear.text = car.releaseYear.toString()

        binding.tvGearbox.text = car.gearboxName
        binding.tvDrive.text = car.driveName

        binding.tvFuel.text = car.fuelTypeName
        binding.tvEngCapacity.text = car.engineCapacityL.toString()
        binding.tvPowerHp.text = car.enginePowerHP.toString()
        binding.tvPowerKw.text = car.enginePowerKW.toString()

        binding.tvTank.text = car.tankCapacityL.toString()
        binding.tvWeight.text = car.vehicleWeightKG.toString()
    }

    private fun showShortToast(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}