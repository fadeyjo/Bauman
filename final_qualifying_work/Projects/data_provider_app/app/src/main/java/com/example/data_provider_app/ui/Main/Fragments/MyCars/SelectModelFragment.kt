package com.example.data_provider_app.ui.Main.Fragments.MyCars

import BrandAdapter
import ModelAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.data_provider_app.databinding.FragmentSelectBrandBinding
import com.example.data_provider_app.databinding.FragmentSelectModelBinding
import com.example.data_provider_app.ui.Main.DirectoryState
import com.example.data_provider_app.ui.Main.MainViewModel
import kotlinx.coroutines.launch
import kotlin.getValue

class SelectModelFragment : Fragment() {

    private lateinit var binding: FragmentSelectModelBinding
    private lateinit var adapter: ModelAdapter

    private lateinit var allModels: List<String>

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectModelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val brand = arguments?.getString("brand")

        if (brand.isNullOrEmpty()) {
            showShortToast("Не передан брэнд автомобиля")

            return
        }

        observeViewModel()
        viewModel.getAllModels(brand)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.modelsState.collect { state ->
                        when (state) {
                            is DirectoryState.Data -> {
                                allModels = state.items

                                adapter = ModelAdapter(allModels) { selectedModel ->
                                    selectModel(selectedModel)
                                }

                                binding.recyclerModels.layoutManager =
                                    LinearLayoutManager(requireContext())

                                binding.recyclerModels.adapter = adapter

                                binding.etSearchModel.addTextChangedListener {
                                    val query = it.toString().lowercase()

                                    val filtered = allModels.filter { model ->
                                        model.lowercase().contains(query)
                                    }

                                    adapter.updateList(filtered)
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
                }
            }
        }
    }

    private fun showShortToast(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun selectModel(brand: String) {

        parentFragmentManager.setFragmentResult(
            "model_request",
            Bundle().apply {
                putString("model", brand)
            }
        )

        parentFragmentManager.popBackStack()
    }
}