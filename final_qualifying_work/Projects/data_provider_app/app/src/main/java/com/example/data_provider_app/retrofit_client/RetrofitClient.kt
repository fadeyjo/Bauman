package com.example.data_provider_app.retrofit_client

import com.example.data_provider_app.api.CarApi
import com.example.data_provider_app.api.CarBodyApi
import com.example.data_provider_app.api.CarDriveApi
import com.example.data_provider_app.api.CarGearboxApi
import com.example.data_provider_app.api.EngineTypeApi
import com.example.data_provider_app.api.FuelTypeApi
import com.example.data_provider_app.api.GPSDataApi
import com.example.data_provider_app.api.PersonApi
import com.example.data_provider_app.api.TelemetryDataApi
import com.example.data_provider_app.api.TripApi
import com.example.data_provider_app.repository.CarBodiesRepository
import com.example.data_provider_app.repository.CarDrivesRepository
import com.example.data_provider_app.repository.CarGearboxesRepository
import com.example.data_provider_app.repository.CarsRepository
import com.example.data_provider_app.repository.EngineTypesRepository
import com.example.data_provider_app.repository.FuelTypesRepository
import com.example.data_provider_app.repository.GPSDataRepository
import com.example.data_provider_app.repository.PersonsRepository
import com.example.data_provider_app.repository.TelemetryDataRepository
import com.example.data_provider_app.repository.TripsRepository
import com.example.data_provider_app.util.LocalDateTimeAdapter
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object RetrofitClient {
    private class LocalDateAdapter : TypeAdapter<LocalDate>() {

        private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

        override fun write(out: JsonWriter, value: LocalDate?) {
            out.value(value?.format(formatter))
        }

        override fun read(reader: JsonReader): LocalDate? {
            return LocalDate.parse(reader.nextString(), formatter)
        }
    }

    private const val BASE_URL: String = "https://192.168.3.2:5001/api/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
        .create()
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val carBodiesRepository by lazy {
        CarBodiesRepository(retrofit.create(CarBodyApi::class.java))
    }

    val carDrivesRepository by lazy {
        CarDrivesRepository(retrofit.create(CarDriveApi::class.java))
    }

    val carGearboxesRepository by lazy {
        CarGearboxesRepository(retrofit.create(CarGearboxApi::class.java))
    }

    val carsRepository by lazy {
        CarsRepository(retrofit.create(CarApi::class.java))
    }

    val engineTypesRepository by lazy {
        EngineTypesRepository(retrofit.create(EngineTypeApi::class.java))
    }

    val fuelTypesRepository by lazy {
        FuelTypesRepository(retrofit.create(FuelTypeApi::class.java))
    }

    val GPSDataRepository by lazy {
        GPSDataRepository(retrofit.create(GPSDataApi::class.java))
    }

    val personsRepository by lazy {
        PersonsRepository(retrofit.create(PersonApi::class.java))
    }

    val telemetryDataRepository by lazy {
        TelemetryDataRepository(retrofit.create(TelemetryDataApi::class.java))
    }

    val tripRepository by lazy {
        TripsRepository(retrofit.create(TripApi::class.java))
    }
}