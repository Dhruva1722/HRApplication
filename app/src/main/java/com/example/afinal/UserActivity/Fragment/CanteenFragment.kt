package com.example.afinal.UserActivity.Fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.afinal.R
import com.example.afinal.UserActivity.ApiService
import com.example.afinal.UserActivity.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CanteenFragment : Fragment() {

    private lateinit var foodListView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var buyBtn: Button
    private lateinit var tommorowMenuCard :LinearLayout
    private lateinit var sharedPreferences: SharedPreferences

    private var userId: String? = null
    private var hasPurchased = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_canteen, container, false)



        foodListView = view.findViewById(R.id.foodListView)

        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val storedPurchaseDate = sharedPreferences.getString("purchaseDate", "")
        userId = sharedPreferences.getString("User",null)


        val currentDateTime = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        buyBtn = view.findViewById(R.id.bookBtnId)

        if (storedPurchaseDate != currentDateTime) {
            buyBtn.isEnabled = true
        } else {
            buyBtn.isEnabled = false
        }

        buyBtn.setOnClickListener {
            if (!hasPurchased) {
                val numberOfCoupons = 1
                val purchaseDate = currentDateTime
                userId?.let { it1 -> makePurchase(it1, numberOfCoupons, purchaseDate) }
//                buyBtn.isEnabled = false

                hasPurchased = true
               Toast.makeText(context, "This Coupen is valid till today", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "You have already made a purchase today.", Toast.LENGTH_SHORT).show()
            }

        }

        // Initialize an empty adapter
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1)
        foodListView.adapter = adapter
        foodListView.choiceMode = ListView.CHOICE_MODE_SINGLE


        val apiService = RetrofitClient.getClient().create(ApiService::class.java)
        Log.d("============", "onResponse:  " + apiService)
            // Make the API request to fetch menu data
            apiService.getMenu().enqueue(object : Callback<MenuData> {
                override fun onResponse(call: Call<MenuData>, response: Response<MenuData>) {
                    if (response.isSuccessful) {
                        val menuData = response.body()
                        menuData?.today?.menu?.let { todayMenuText ->
                            val menuItemsArray = todayMenuText.split(", ")
                            adapter.addAll(menuItemsArray)
                        }
                    } else {
                        Toast.makeText(requireContext(),"Data not find",Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<MenuData>, t: Throwable) {
                    Toast.makeText(requireContext(),"Network errorrr",Toast.LENGTH_SHORT).show()
                }
            })

        return view
    }



    private fun makePurchase(userId: String, numberOfCoupons: Int, purchaseDate: String) {



        val purchaseData = PurchaseData(
            userId = userId,
            numberOfCoupons = numberOfCoupons,
            purchaseDate = purchaseDate
        )
        val apiService = RetrofitClient.getClient().create(ApiService::class.java)
        val call = apiService.buyMenuItems(purchaseData)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Purchase successful
                    Toast.makeText(context, "Purchase successful", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle the case where the request was not successful
                    Toast.makeText(context, "Purchase failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Handle network errors here
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

private fun <T> Call<T>.enqueue(callback: Callback<MenuData>) {
    TODO("Not yet implemented")
}


data class MenuData(
    val today: Canteen?,
    val tomorrow: Canteen?
)

data class Canteen(
    val date: String?,
    val menu: String?,
    val status: String?
)


data class PurchaseData(
    val userId: String,
    val numberOfCoupons: Int,
    val purchaseDate: String // Format: "YYYY-MM-DD"
)