package com.example.afinal.UserActivity


import com.example.afinal.UserActivity.Fragment.AttendanceData
import com.example.afinal.UserActivity.Fragment.Event
import com.example.afinal.UserActivity.Fragment.LocationInfo
import com.example.afinal.UserActivity.Fragment.MenuData
import com.example.afinal.UserActivity.Fragment.PurchaseData
import com.example.afinal.UserActivity.Fragment.PurchaseData1
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path


interface ApiService {

    @POST("/empregister")
    fun registerUser(@Body registrationData: JsonObject): Call<Any>

    @POST("/emplogin")
    fun loginRequest(@Body credentials: JsonObject): Call<LoginResponse>

    @POST("location/{id}")
    fun postLocationData(
        @Path("id") userId: String?,
        @Body locationData: LocationInfo
    ): Call<Any>

//    @GET("/location/{id}")
//    fun getTotalDistance(@Path("id") userId: String): Call<DistanceResponse>

    @POST("/attendance")
    fun saveAttendance (@Body attendanceData: AttendanceData): Call<Void>

    @POST("/leave/{id}")
    fun postLeaveRequest(
        @Path("id") userId: String,
        @Body leaveRequest: LeaveRequest
    ): Call<ResponseBody>

    @GET("/leave/{id}")
    fun getLeaveApplications(@Path("id") userId: String): Call<LeaveResponse>

    @POST("/latLong")
    fun saveLocationData(@Body locationData: com.example.afinal.MapActivity.LocationData): Call<Any>

//    @POST("/form")
//    fun saveFormData(@Body formData: FormData): Call<Void>

    @Multipart
    @POST("/form")
    fun saveFormData(
        @Part("userId") userId: RequestBody,
        @Part("Transport_type") transportType: RequestBody,
        @Part("Total_expense") totalexpense: RequestBody,
        @Part("Fuel_in_liters") fuelInLiters: RequestBody,
        @Part("Food") food: RequestBody,
        @Part("Water") water: RequestBody,
        @Part("Hotel") hotel: RequestBody,
        @Part("Other_Transport") otherTransport: RequestBody,
        @Part image: MultipartBody.Part // Add RequestBody for the name
    ): Call<Void>
    @POST("/complaint")
    fun submitComplaint(@Body data: ComplaintRequest): Call<Any>

    @PUT("/Users/{id}")
    fun updateUserData(
        @Path("id") userId: String,
        @Body employeeUpdateData: UserData
    ): Call<Void>

//    @GET("/manager")
//    fun getManagers(): Call<List<Manager>>

    @GET("/menu")
    fun getMenu(): Call<MenuData>

    @GET("/empdata")
    fun getManagers(): List<Employee>

    @POST("/menu/buy")
    fun buyMenuItems(@Body purchaseData: PurchaseData): Call<Void>

    @POST("/menu/buy")
    fun buyMenuItems(@Body purchaseData: PurchaseData1): Call<Void>

    @GET("/event")
    fun getEvents(): Call<List<Event>>


    @GET("user/{Id}") // Replace with your actual endpoint
    fun getUserData(@Path("Id") userId: String): Call<User>

    @GET("/location/{Id}")
    fun getTripData(@Path("Id") userId: String):  Call<List<TripInfo>>


    @Multipart
    @POST("update-profile/{Id}")
    suspend fun uploadImage(@Path("Id") userId: String , @Part image: MultipartBody.Part): Response<String>

//    @GET("/form")
//    fun getImageData(@Body data: ImageData): Call<Any>


}
