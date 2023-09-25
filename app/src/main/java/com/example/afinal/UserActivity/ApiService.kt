package com.example.afinal.UserActivity

import com.example.afinal.UserActivity.Fragment.AttendanceData
import com.example.afinal.UserActivity.Fragment.LocationData
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {

    @POST("/empregister")
    fun registerUser(@Body registrationData: JsonObject): Call<Any>


    @POST("/emplogin")
    fun loginRequest(@Body credentials: JsonObject): Call<LoginResponse>


    @POST("/location")
    fun postLocationData(@Body locationData: LocationData): Call<Any>
//    fun postLocationData(@Body locationData: LocationData, @Header("authorization") token: Map<String, String>): Call<Any>

    @POST("/attandance")
    fun saveAttendance(@Body attendanceData: AttendanceData): Call<Void>


    @POST("/latLong")
    fun saveLocationData(@Body locationData: com.example.afinal.MapActivity.LocationData): Call<Any>

    @POST("/form") // Adjust the endpoint URL as needed
    fun saveClearanceData(@Body requestData: TransportationData): Call<Any>
}
