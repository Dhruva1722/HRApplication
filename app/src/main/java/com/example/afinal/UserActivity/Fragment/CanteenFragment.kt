package com.example.afinal.UserActivity.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.example.afinal.R
import com.example.afinal.UserActivity.Adapter.ManagerAdapter
import com.example.afinal.UserActivity.ApiService
import com.example.afinal.UserActivity.Manager
import com.example.afinal.UserActivity.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CanteenFragment : Fragment() {

    private lateinit var foodListView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_canteen, container, false)

        foodListView = view.findViewById(R.id.foodListView)

        // Initialize an empty adapter
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1)
        foodListView.adapter = adapter
        foodListView.choiceMode = ListView.CHOICE_MODE_SINGLE

        // Make an API request to get food items
        val apiService = RetrofitClient.getClient().create(ApiService::class.java)
        val call = apiService.getFoodMenu()
        call.enqueue(object : Callback<List<FoodItem>> {
            override fun onResponse(call: Call<List<FoodItem>>, response: Response<List<FoodItem>>) {
                if (response.isSuccessful) {
                    val foodItems = response.body()
                    if (foodItems != null) {
                        for (item in foodItems) {
                            // Add each food item to the adapter
                            adapter.add(item.menu)
                        }
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Handle API request failure here
                    Toast.makeText(requireContext(), "API request failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<FoodItem>>, t: Throwable) {
                // Handle network error here
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
            }
        })

        return view
    }
}

private fun Any.enqueue(callback: Callback<List<FoodItem>>) {

}

data class FoodResponse(
    val menu: List<FoodItem>
)

data class FoodItem(
    val menu: String
)