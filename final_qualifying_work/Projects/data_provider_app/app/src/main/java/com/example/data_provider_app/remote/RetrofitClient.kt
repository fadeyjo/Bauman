package com.example.data_provider_app.remote

import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
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
        .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
        .create()
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val api = retrofit.create(ApiService::class.java)
}