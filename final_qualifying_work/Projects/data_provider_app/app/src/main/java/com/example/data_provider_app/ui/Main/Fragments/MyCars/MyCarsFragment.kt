package com.example.data_provider_app.ui.Main.Fragments.MyCars

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.data_provider_app.R
import com.example.data_provider_app.ui.Main.MainViewModel
import com.example.data_provider_app.ui.Main.MyCarsState
import kotlinx.coroutines.launch
import kotlin.getValue

class MyCarsFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var adapter: CarAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_my_cars, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity)
            .supportActionBar
            ?.title = "Мои автомобили"

        recyclerView = view.findViewById(R.id.recyclerViewCars)
        tvEmpty = view.findViewById(R.id.tvEmptyCars)

        setupRecyclerView()

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_my_cars, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_add_car -> {
                        openAddCarFragment()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        observeCars()

        viewModel.getMyCars()
    }

    private fun openAddCarFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, AddCarFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun setupRecyclerView() {
        adapter = CarAdapter(emptyList()) { car ->
            openCarDetailFragment(car.vinNumber)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun openCarDetailFragment(VIN: String) {
        val fragment = CarDetailFragment.newInstance(VIN)

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showShortToast(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun observeCars() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.myCarsState.collect { state ->
                    when (state) {
                        is MyCarsState.Data -> {
                            if (state.cars.isEmpty()) {
                                recyclerView.visibility = View.GONE
                                tvEmpty.visibility = View.VISIBLE
                            } else {
                                recyclerView.visibility = View.VISIBLE
                                tvEmpty.visibility = View.GONE
                                adapter.updateData(state.cars)
                            }
                        }
                        is MyCarsState.Error -> showShortToast(state.message)
                        is MyCarsState.Idle -> {}
                        is MyCarsState.Loading -> {}
                        is MyCarsState.NetworkError -> showShortToast("Нет подключения к интернету")
                        is MyCarsState.UnknownError -> showShortToast("Неизвестная ошибка")
                        is MyCarsState.ValidationError -> showShortToast("Ошибка валидации данных")
                    }
                }
            }
        }
    }
}