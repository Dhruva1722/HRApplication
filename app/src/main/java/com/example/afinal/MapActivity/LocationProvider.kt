package com.example.afinal.MapActivity

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.viewmodel.CreationExtras.Empty.map
import com.example.afinal.UserActivity.ApiService
import com.example.afinal.UserActivity.RetrofitClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt



@SuppressLint("MissingPermission")
class LocationProvider(private val activity: AppCompatActivity) {


    private val client by lazy { LocationServices.getFusedLocationProviderClient(activity) }

    private val locations = mutableListOf<LatLng>()
    private var distance = 0

    private val SMOOTHING_WINDOW_SIZE = 126

    private val smoothedLocations = mutableListOf<LatLng>()

    val liveLocations = MutableLiveData<List<LatLng>>()
    val liveDistance = MutableLiveData<Int>()
    val liveLocation = MutableLiveData<LatLng>()
    val liveAddress = MutableLiveData<String>()

    private lateinit var userId: String
    fun isLocationEnabled(): Boolean {
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {

            val sharedPreferences = activity.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            userId = sharedPreferences.getString("User", null) ?: ""

            val currentLocation = result.lastLocation
            if (currentLocation != null) {
                val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)

                // Get the current address using Geocoder
                fetchCurrentAddress(latLng)

                // Print location details to console
                Log.d(
                    latLng.toString(),
                    "lat , lon" + "-------------" + currentLocation.latitude + "----------" + currentLocation.longitude
                )

                val timeStamp = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
                val locationInfo1 =
                   "Time: ${timeStamp}, Latitude: ${currentLocation.latitude}, Longitude: ${currentLocation.longitude}"
                Log.d("LocationInfo", locationInfo1)

                val lastLocation = locations.lastOrNull()
                if (lastLocation != null) {
                    val distanceToCurrent =
                        SphericalUtil.computeDistanceBetween(lastLocation, latLng)
                    if (distanceToCurrent >= 10.0) {
                        distance += distanceToCurrent.roundToInt()
                        liveDistance.value = distance
                        Log.d("Distance", "Total distance: $distance meters")
                    }
                }
                // Add the latLng to the smoothedLocations
                smoothedLocations.add(latLng)

                // Keep the size of smoothedLocations within a certain limit (e.g., 5 data points)
                if (smoothedLocations.size > SMOOTHING_WINDOW_SIZE) {
                    smoothedLocations.removeAt(0)
                }

                // Calculate the smoothed latitude and longitude
                val smoothedLatLng = calculateSmoothedLatLng(smoothedLocations)

                locations.add(smoothedLatLng) // Use smoothed location for distance calculations
                liveLocations.value = locations

                val latitude = smoothedLatLng.latitude // Use smoothed latitude
                val longitude = smoothedLatLng.longitude // Use smoothed longitude

                val locationData = LocationData(userId, latitude, longitude, distance)
                saveLocationDataToApi(locationData)
                saveLocationToFile(locationInfo1)
            }
        }
    }

    private fun saveLocationDataToApi(locationData: LocationData) {

        val apiService = RetrofitClient.getClient().create(ApiService::class.java)

        val call = apiService.saveLocationData(locationData)

        call.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (response.isSuccessful) {
                    // Handle a successful response if needed
                    Log.d("LocationData", "Location data saved successfully.")
                } else {
                    // Handle an error response if needed
                    Log.e("LocationData", "Failed to save location data.")
                }
            }
            override fun onFailure(call: Call<Any>, t: Throwable) {
                // Handle network or other errors if needed
                Log.e("LocationData", "Network error: ${t.message}")
            }
        })
    }

    private fun saveLocationToFile(locationInfo: String) {
        try {
            val fileName = "location_data.txt"
            val fileOutputStream = activity.openFileOutput(fileName, Context.MODE_APPEND)
            val outputStreamWriter = OutputStreamWriter(fileOutputStream)
            outputStreamWriter.write(locationInfo + "\n")
            outputStreamWriter.close()
            Log.d("LocationSaved", "Location data saved to file.")
        } catch (e: Exception) {
            Log.e("LocationSaveError", "Error saving location data: ${e.message}")
        }
    }

    private fun calculateSmoothedLatLng(locations: List<LatLng>): LatLng {
        var sumLat = 0.0
        var sumLng = 0.0

        for (location in locations) {
            sumLat += location.latitude
            sumLng += location.longitude
        }
        val smoothedLat = sumLat / locations.size
        val smoothedLng = sumLng / locations.size

        return LatLng(smoothedLat, smoothedLng)
    }
    // Method to fetch the current address using Geocoder
    private fun fetchCurrentAddress(latLng: LatLng) {
        val geocoder = Geocoder(activity)
        val addressList: List<Address> = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1) as List<Address>

        if (addressList.isNotEmpty()) {
            val address = addressList[0]
            val addressText = address.getAddressLine(0) // Get the first address line
            // Update the LiveData for the current address
            liveAddress.value = addressText
        } else {
            liveAddress.value = null
        }
    }
    fun getUserLocation() {
        client.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                locations.add(latLng)
                liveLocation.value = latLng
            } else {
                // Location is null, request location updates explicitly
                trackUser()
            }
        }
    }
    fun trackUser() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            interval = 10000 // Update interval in milliseconds (10 seconds)
            fastestInterval = 50000 // Minimum time interval between updates in milliseconds (5 seconds)
        }
        client.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    fun stopTracking() {
        client.removeLocationUpdates(locationCallback)
        locations.clear()
        distance = 0
    }
  }
data class LocationData(
    val userId: String,
    val latitude: Double,
    val longitude: Double,
    var distance: Int
)



//saveLocationToFile(locationInfo)

// harvies formula
//fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
//    val R = 6371 // Earth's radius in kilometers
//
//    val dLat = deg2rad(lat2 - lat1)
//    val dLon = deg2rad(lon2 - lon1)
//    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
//            Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
//            Math.sin(dLon / 2) * Math.sin(dLon / 2)
//    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
//    val d = R * c // Distance in km
//    return d
//}
//
//fun deg2rad(deg: Double): Double {
//    return deg * (Math.PI / 180)
//}


//                if (currentLocation.speed >= 1.0) {
//                    val lastLocation = locations.lastOrNull()
//                    if (lastLocation != null) {
//                        val distanceToCurrent = calculateDistance(
//                            lastLocation.latitude, lastLocation.longitude,
//                            latLng.latitude, latLng.longitude
//                        )
//                        distance += distanceToCurrent.roundToInt()
//                        liveDistance.value = distance
//                    }
//                }
//
//                locations.add(latLng)
//                liveLocations.value = locations


//                if (lastLocation != null) {
//                    val distanceToCurrent =
//                        SphericalUtil.computeDistanceBetween(lastLocation, latLng)
//                    distance += distanceToCurrent.roundToInt()
//                    liveDistance.value = distance
//                }
//
//                locations.add(latLng)
//                liveLocations.value = locations
//            }

//        client.lastLocation.addOnSuccessListener { location ->
//            val latLng = LatLng(location.latitude, location.longitude)
//            locations.add(latLng)
//            liveLocation.value = latLng
//        }