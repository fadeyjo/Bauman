package com.example.data_provider_app.ui.Main.Fragments.MyCars

import BrandAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.data_provider_app.R
import com.example.data_provider_app.databinding.FragmentSelectBrandBinding
import com.example.data_provider_app.ui.Main.DirectoryState
import com.example.data_provider_app.ui.Main.MainViewModel
import kotlinx.coroutines.launch
import kotlin.getValue

class SelectBrandFragment : Fragment() {

    private lateinit var binding: FragmentSelectBrandBinding
    private lateinit var adapter: BrandAdapter

    private lateinit var allBrands: List<String>

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectBrandBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeViewModel()
        viewModel.getAllBrands()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.brandsState.collect { state ->
                        when (state) {
                            is DirectoryState.Data -> {
                                allBrands = state.items

                                adapter = BrandAdapter(allBrands) { selectedBrand ->
                                    selectBrand(selectedBrand)
                                }

                                binding.recyclerBrands.layoutManager =
                                    LinearLayoutManager(requireContext())

                                binding.recyclerBrands.adapter = adapter

                                binding.etSearch.addTextChangedListener {
                                    val query = it.toString().lowercase()

                                    val filtered = allBrands.filter { brand ->
                                        brand.lowercase().contains(query)
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

    private fun selectBrand(brand: String) {

        parentFragmentManager.setFragmentResult(
            "brand_request",
            Bundle().apply {
                putString("brand", brand)
            }
        )

        parentFragmentManager.popBackStack()
    }
}