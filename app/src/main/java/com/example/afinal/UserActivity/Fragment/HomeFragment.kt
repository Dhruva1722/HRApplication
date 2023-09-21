package com.example.afinal.UserActivity.Fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.afinal.MapActivity.MapsActivity
import com.example.afinal.R
import com.example.afinal.UserActivity.ApiService
import com.example.afinal.UserActivity.LoginActivity
import com.example.afinal.UserActivity.RegistrationActivity
import com.example.afinal.UserActivity.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

private val Unit.isSuccessful: Boolean
    get() {return true}

class HomeFragment : Fragment() {

        private lateinit var continuebtn : Button
        private lateinit var yourLocation : TextInputEditText
        private lateinit var destinationLocation : TextInputEditText

        private lateinit var sharedPreferences: SharedPreferences

        private lateinit var tokenManager: LoginActivity.TokenManager
        private val gson = Gson()
        private lateinit var apiService: ApiService


        private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("User", null)

        tokenManager = LoginActivity.TokenManager(requireContext())

        continuebtn = view.findViewById(R.id.continueBtn)
        yourLocation = view.findViewById(R.id.StartPointID)
        destinationLocation = view.findViewById(R.id.EndPointID)

        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        apiService = RetrofitClient.getClient().create(ApiService::class.java)

        continuebtn.setOnClickListener {
            val startPoint = yourLocation.text.toString()
            val endPoint = destinationLocation.text.toString()

            val token = tokenManager.getToken()
            if (userId != null) {
                val locationData = LocationData(userId!!, startPoint, endPoint)
                Log.d("+++++++++++++", "onCreateView: location data " + locationData)
                val headers = mapOf("Authorization" to "Bearer --- $token")

                Log.d("=====", "onCreateView: header token  " + headers)
                val call = apiService.postLocationData(locationData, headers)
                call.enqueue(object : Callback<Any> {
                    override fun onResponse(call: Call<Any>, response: Response<Any>) {
                        if (response.isSuccessful) {
                            Toast.makeText(activity, "successs", Toast.LENGTH_SHORT).show()
                            val intent = Intent(activity, MapsActivity::class.java)
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

}


data class LocationData(
    val userId :String,
    val startPoint: String,
    val endPoint: String
)