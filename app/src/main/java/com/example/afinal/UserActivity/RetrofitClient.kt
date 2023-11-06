package com.example.afinal.UserActivity

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

//    private const val BASE_URL = "https://dashboardbackend-production-9839.up.railway.app/"
private const val BASE_URL = "http://192.168.1.211:8080"
    private var retrofit: Retrofit? = null

    fun getClient(): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

}

