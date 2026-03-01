package com.example.data_provider_app.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.example.data_provider_app.retrofit_client.RetrofitClient
import java.io.InputStream

@GlideModule
class MyAppGlideModule : AppGlideModule() {
    override fun registerComponents(
        context: Context,
        glide: Glide,
        registry: Registry
    ) {
        val factory = OkHttpUrlLoader.Factory(RetrofitClient.client)
        registry.replace(GlideUrl::class.java, InputStream::class.java, factory)
    }
}