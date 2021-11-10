package com.udacity.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit

object RetrofitClient {

    private const val API_HOST = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/"


    fun provideApi(): RetrofitApi = Retrofit.Builder()
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(API_HOST)
        .build()
        .create(RetrofitApi::class.java)

}