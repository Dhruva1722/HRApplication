package com.example.afinal.UserActivity

import com.example.afinal.UserActivity.Fragment.LocationData
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST


interface ApiService {

    @POST("/empregister")
    fun registerUser(@Body registrationData: JsonObject): Call<Any>


    @POST("/emplogin")
    fun loginRequest(@Body credentials: JsonObject): Call<LoginResponse>


    @POST("/location")
    fun postLocationData(
        @Header("Authorization") token: LocationData,
        @Body locationData: Map<String, String>
    ): Call<Any>

    @GET("/getlocation")
    fun getLocationPoints(@Header("Authorization") token: String): Call<List<LocationData>>



}
