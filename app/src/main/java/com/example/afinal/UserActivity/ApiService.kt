package com.example.afinal.UserActivity


import com.example.afinal.UserActivity.Fragment.AttendanceData
import com.example.afinal.UserActivity.Fragment.Event
import com.example.afinal.UserActivity.Fragment.LocationData
import com.example.afinal.UserActivity.Fragment.MenuData
import com.example.afinal.UserActivity.Fragment.PurchaseData
import com.example.afinal.UserActivity.Fragment.PurchaseData1
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiService {

    @POST("/empregister")
    fun registerUser(@Body registrationData: JsonObject): Call<Any>


    @POST("/emplogin")
    fun loginRequest(@Body credentials: JsonObject): Call<LoginResponse>


    @POST("/location")
    fun postLocationData(@Body locationData: LocationData): Call<Any>

    @POST("/attandance")
    fun saveAttendance(@Body attendanceData: AttendanceData): Call<Void>


    @POST("/latLong")
    fun saveLocationData(@Body locationData: com.example.afinal.MapActivity.LocationData): Call<Any>

    @POST("/form")
    fun saveTransportationData(@Body data: TransportationData): Call<Any>

    @POST("/complaint")
    fun submitComplaint(@Body data: ComplaintRequest): Call<Any>

    @GET("/manager")
    fun getManagers(): Call<List<Manager>>

    @GET("/menu")
    fun getMenu(): Call<MenuData>

    @POST("/menu/buy")
    fun buyMenuItems(@Body purchaseData: PurchaseData): Call<Void>
    @POST("/menu/buy")
    fun buyMenuItems(@Body purchaseData: PurchaseData1): Call<Void>

    @GET("/event")
    fun getEvents(): Call<List<Event>>

//    @GET("/form")
//    fun getImageData(@Body data: ImageData): Call<Any>


}
