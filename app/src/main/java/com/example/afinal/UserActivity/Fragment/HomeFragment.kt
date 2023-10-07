package com.example.afinal.UserActivity.Fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.afinal.MapActivity.MapsActivity
import com.example.afinal.R
import com.example.afinal.UserActivity.ApiService
import com.example.afinal.UserActivity.LoginActivity
import com.example.afinal.UserActivity.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.Locale


class HomeFragment : Fragment() {

    private lateinit var continuebtn : Button
    private lateinit var yourLocation : TextInputEditText
    private lateinit var destinationLocation : TextInputEditText

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var apiService: ApiService

    private var userId: String? = null


    private lateinit var geocoder: Geocoder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("User",null)
        Log.d("+++++++++++++", "user ID--- " + userId)


        continuebtn = view.findViewById(R.id.continueBtn)
        yourLocation = view.findViewById(R.id.StartPointID)
        destinationLocation = view.findViewById(R.id.EndPointID)

        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        apiService = RetrofitClient.getClient().create(ApiService::class.java)

        geocoder = Geocoder(requireContext(), Locale.getDefault())

        continuebtn.setOnClickListener {
            val startPoint = yourLocation.text.toString()
            val endPoint = destinationLocation.text.toString()

            val startLocation = findLocation(startPoint)
            val endLocation = findLocation(endPoint)

            if (startLocation != null && endLocation != null) {
                // You can access latitude and longitude like this:
                val startLatitude = startLocation.latitude
                val startLongitude = startLocation.longitude
                val endLatitude = endLocation.latitude
                val endLongitude = endLocation.longitude

                // Now you have the latitude and longitude for both start and end points
                Log.d("--------------", "Start Latitude: $startLatitude, Start Longitude: $startLongitude")
                Log.d("++++++++++++++", "End Latitude: $endLatitude, End Longitude: $endLongitude")

                // Proceed with your logic
            } else {
                Toast.makeText(activity, "Location not found", Toast.LENGTH_SHORT).show()
            }
            if (userId != null) {
                val locationData = LocationData(userId!!, startPoint, endPoint)
                Log.d("+++++++++++++", "onCreateView: location data " + locationData)

                val call = apiService.postLocationData(locationData)
                call.enqueue(object : Callback<Any> {
                    override fun onResponse(call: Call<Any>, response: Response<Any>) {
                        if (response.isSuccessful) {
                            Toast.makeText(activity, "successs", Toast.LENGTH_SHORT).show()
                            val intent = Intent(activity, MapsActivity::class.java)
                            intent.putExtra("startPoint", startPoint)
                            intent.putExtra("endPoint", endPoint)
                            startActivity(intent)
                        } else {
                            Toast.makeText(activity, "failll", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Any>, t: Throwable) {
                        Toast.makeText(activity, "network error", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(activity, "User ID is null", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }
    private fun findLocation(locationName: String): Address? {
        try {
            val addresses: List<Address>? = geocoder.getFromLocationName(locationName, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                Log.d("Location", "Latitude: ${address.latitude}, Longitude: ${address.longitude}")
                Log.d("**********", "findLocation: \"Latitude: ${address.latitude}, Longitude: ${address.longitude}\"")
                return address
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("Location", "Error finding location: ${e.message}")
        }
        return null
    }
}
data class LocationData(
    val userId :String,
    val startPoint: String,
    val endPoint: String
)