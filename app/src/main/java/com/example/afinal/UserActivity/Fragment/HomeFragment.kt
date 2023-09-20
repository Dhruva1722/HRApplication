package com.example.afinal.UserActivity.Fragment

import android.content.Intent
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
import com.example.afinal.UserActivity.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

        private lateinit var continuebtn : Button
        private lateinit var yourLocation : TextInputEditText
        private lateinit var destinationLocation : TextInputEditText

        private val gson = Gson()
         private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)


        apiService = RetrofitClient.getClient().create(ApiService::class.java)

        continuebtn = view.findViewById(R.id.continueBtn)
        yourLocation = view.findViewById(R.id.StartPointID)
        destinationLocation = view.findViewById(R.id.EndPointID)

        continuebtn.setOnClickListener {
            val startPoint = yourLocation.text.toString()
            val endPoint = destinationLocation.text.toString()

            // Create a list of LocationData objects
            val locationList = listOf(
                LocationData(startPoint, endPoint)
            )

            val locationListJson = gson.toJson(locationList)


            Log.d("-----------", "onCreate: location data $locationList")

            val call =apiService.locationRequest(locationListJson)
            call.enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            context,
                            "Location Points Successfully saved",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(activity, MapsActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            context,
                            "Location Points failed to save",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Toast.makeText(context, "Network error ", Toast.LENGTH_SHORT).show()
                }
            })
        }
            return view
        }
}

data class LocationData(
    val startPoint: String,
    val endPoint: String
)


