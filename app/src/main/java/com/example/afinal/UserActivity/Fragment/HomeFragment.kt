package com.example.afinal.UserActivity.Fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.afinal.R
import com.example.afinal.UserActivity.ApiService
import com.example.afinal.UserActivity.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Unit.isSuccessful: Boolean
    get() {return true}

class HomeFragment : Fragment() {

        private lateinit var continuebtn : Button
        private lateinit var yourLocation : TextInputEditText
        private lateinit var destinationLocation : TextInputEditText

        private lateinit var sharedPreferences: SharedPreferences

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

        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        continuebtn.setOnClickListener {
            val startPoint = yourLocation.text.toString()
            val endPoint = destinationLocation.text.toString()

            val userId = sharedPreferences.getString("User", "")

            val locationData = LocationData(userId, startPoint, endPoint)

            lifecycleScope.launch {
                sendLocationData(locationData)
            }
        }
            return view
        }

    private suspend fun sendLocationData(locationData: LocationData) {
        val token = sharedPreferences.getString("token", "")
        Log.d("---------", "sendLocationData: token" + token)

        if (!token.isNullOrEmpty()) {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.sendLocationData("Bearer $token", locationData).execute()
                }

                if (response.isSuccessful) {
                    // Location data sent successfully
                    Toast.makeText(context, "Location data sent", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle errors
                    Toast.makeText(context, "Failed to send location data", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Handle exceptions
                Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Handle missing or expired token
            Toast.makeText(context, "Token is missing or expired", Toast.LENGTH_SHORT).show()
        }
    }
}

private fun <T> Response<T>.execute() {
    TODO("Not yet implemented")
}



data class LocationData(
    val userId: String?,
    val startPoint: String,
    val endPoint: String
)

