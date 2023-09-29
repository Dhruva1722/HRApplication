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


        val apiService = RetrofitClient.getClient().create(ApiService::class.java)
        Log.d("============", "onResponse:  " + apiService)
        val call = apiService.getFoodMenu()
        call.enqueue(object :  Callback<MenuData?> {
            override fun onResponse(call: Call<MenuData?>, response: Response<MenuData?>) {
                Log.d("============", "onResponse: ${response} ")
                if (response.isSuccessful) {
                    val menuData = response.body()
                    if (menuData != null && menuData.today != null) {
                        adapter.add(menuData.today.menu)
                        Log.d("============", "onResponse: ${menuData.today} ")
                    } else {
                        Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "API request failed", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<MenuData?>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
            }
        })
        return view
    }
}
private fun <T> Call<T>.enqueue(callback: Callback<MenuData?>) {

}

data class MenuData(val today: MenuItems, val tomorrow: MenuItems)

data class MenuItems(val menu: String)