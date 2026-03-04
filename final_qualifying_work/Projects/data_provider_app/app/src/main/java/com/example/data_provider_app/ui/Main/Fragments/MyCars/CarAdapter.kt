package com.example.data_provider_app.ui.Main.Fragments.MyCars

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.data_provider_app.BuildConfig
import com.example.data_provider_app.dto.CarDto
import com.example.data_provider_app.R
import com.example.data_provider_app.glide.GlideApp

class CarAdapter(
    private var cars: List<CarDto>,
    private val onItemClick: (CarDto) -> Unit
) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {
    inner class CarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPhoto: ImageView = view.findViewById(R.id.ivCarPhoto)
        val tvBrandModel: TextView = view.findViewById(R.id.tvBrandModel)
        val tvPlate: TextView = view.findViewById(R.id.tvPlate)
        val tvEngineVolume: TextView = view.findViewById(R.id.tvEngineVolume)
        val tvHorsePower: TextView = view.findViewById(R.id.tvHorsePower)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_car, parent, false)
        return CarViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = cars[position]

        holder.tvBrandModel.text = "${car.brandName} ${car.modelName}"
        holder.tvPlate.text = "Гос номер: ${car.stateNumber ?: "-"}"
        holder.tvEngineVolume.text = "Объём: ${car.engineCapacityL} л"
        holder.tvHorsePower.text = "Мощность: ${car.enginePowerHP} л.с."

        val imageUrl = BuildConfig.BASE_URL + "api/carphotos/photo_id/${car.photoId}"

        GlideApp.with(holder.itemView)
            .load(imageUrl)
            .placeholder(R.drawable.loading)
            .error(R.drawable.no_image)
            .into(holder.ivPhoto)

        holder.itemView.setOnClickListener {
            onItemClick(car)
        }
    }

    override fun getItemCount() = cars.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newCars: List<CarDto>) {
        cars = newCars
        notifyDataSetChanged()
    }
}