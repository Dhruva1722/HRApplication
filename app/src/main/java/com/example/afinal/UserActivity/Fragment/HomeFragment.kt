package com.example.afinal.UserActivity.Fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.afinal.MainActivity
import com.example.afinal.MapActivity.MapsActivity
import com.example.afinal.R
import com.example.afinal.UserActivity.ApiService
import com.example.afinal.UserActivity.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.Locale


class HomeFragment : Fragment() {

    private lateinit var continuebtn: Button
    private lateinit var yourLocation: TextInputEditText
    private lateinit var destinationLocation: TextInputEditText
    private lateinit var totalDistanceTextView:TextView
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
        userId = sharedPreferences.getString("User", null)
        Log.d("+++++++++++++", "user ID--- " + userId)


        continuebtn = view.findViewById(R.id.continueBtn)
        yourLocation = view.findViewById(R.id.StartPointID)
        destinationLocation = view.findViewById(R.id.EndPointID)

        totalDistanceTextView = view.findViewById(R.id.totalDistanceTxt)

        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        apiService = RetrofitClient.getClient().create(ApiService::class.java)

        geocoder = Geocoder(requireContext(), Locale.getDefault())



        continuebtn.setOnClickListener {
            val intent = Intent(activity, MapsActivity::class.java)
            startActivity(intent)

//            val startPoint = yourLocation.text.toString()
//            val endPoint = destinationLocation.text.toString()
//
//            val startPointLatLng = getLatLngFromAddress(startPoint)
//            val endPointLatLng = getLatLngFromAddress(endPoint)
//
//            if (startPointLatLng != null && endPointLatLng != null) {
//                val locationData = LocationInfo(
//                    StartPoint(
//                        startPoint,
//                        startPointLatLng.first.toString(),
//                        startPointLatLng.second.toString()
//                    ),
//                    EndPoint(
//                        endPoint,
//                        endPointLatLng.first.toString(),
//                        endPointLatLng.second.toString()
//                    )
//                )
//                Log.d("---------------", "onCreateView: points ${locationData}")
//                apiService.postLocationData(  userId,locationData).enqueue(object : Callback<Any> {
//                    override fun onResponse(call: Call<Any>, response: Response<Any>) {
//                        if (response.isSuccessful) {
//                            Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
//                            Log.d("API Success", "Response successful --------")
//                            val handler = Handler()
//                            handler.postDelayed({
//                                val intent = Intent(requireContext(), MainActivity::class.java)
//                                startActivity(intent)
//                            }, 7000)
//                        } else {
//                            Toast.makeText(requireContext(), "Faill to save ", Toast.LENGTH_SHORT)
//                                .show()
//                            Log.e("API Error", response.message())
//                        }
//                    }
//
//                    override fun onFailure(call: Call<Any>, t: Throwable) {
//                        Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT).show()
//                    }
//                })
//            }
        }
            return view
        }

        fun getLatLngFromAddress(address: String): Pair<Double, Double>? {
            try {
                val addresses: List<Address> = geocoder.getFromLocationName(address, 1)!!
                if (addresses.isNotEmpty()) {
                    val latitude = addresses[0].latitude
                    val longitude = addresses[0].longitude
                    return Pair(latitude, longitude)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
}

data class StartPoint(
    val startPointname: String,
    val startLatitude: String,
    val startLongitude: String
)
data class EndPoint(
    val endPointname: String,
    val endLatitude: String,
    val endLongitude: String
)
data class LocationInfo(
    val startPoint: StartPoint,
    val endPoint: EndPoint
)














