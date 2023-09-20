package com.example.afinal.UserActivity

import com.example.afinal.UserActivity.Fragment.LocationData
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface ApiService {

    @POST("/empregister")
    fun registerUser(@Body registrationData: JsonObject): Call<Any>


    @POST("/emplogin")
    fun loginRequest(@Body credentials: JsonObject): Call<LoginResponse>


    @POST("/location")
    suspend fun sendLocationData(
        @Header("Authorization") token: String,
        @Body locationData: LocationData
    ): Response<Any>

}
