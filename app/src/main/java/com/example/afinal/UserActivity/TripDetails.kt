package com.example.afinal.UserActivity

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.example.afinal.R
import com.example.afinal.UserActivity.Adapter.TripAdapter
import com.example.afinal.UserActivity.Fragment.LocationInfo
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TripDetails : AppCompatActivity() {


    private lateinit var sharedPreferences: SharedPreferences
    private var userId: String? = null

    private lateinit var tripAdapter: TripAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_details)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("User", null)

        val apiService = RetrofitClient.getClient().create(ApiService::class.java)
        val call = apiService.getTripData(userId!!)

        val tripDetailsListView: ListView = findViewById(R.id.tripDetails)
        call.enqueue(object : Callback<List<TripInfo>> {
            override fun onResponse(call: Call<List<TripInfo>>, response: Response<List<TripInfo>>) {
                if (response.isSuccessful) {
                    val locationInfoList: List<TripInfo>? = response.body()
                    locationInfoList?.let {
                        tripAdapter = TripAdapter(this@TripDetails, it)
                        tripDetailsListView.adapter = tripAdapter
                    }

                } else {
                    // Handle unsuccessful response
                    Toast.makeText(this@TripDetails, "Failed to fetch location info", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<TripInfo>>, t: Throwable) {
                // Handle network failure
                Toast.makeText(this@TripDetails, "Network error", Toast.LENGTH_SHORT).show()
            }
        })

    }
}

data class TripInfo(
    val startPoint: StartPoint,
    val endPoint: EndPoint,
    val distance: Double,
    val timestamp: String // Adjust the type based on your needs
)

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