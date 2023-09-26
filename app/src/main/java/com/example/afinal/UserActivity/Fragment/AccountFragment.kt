package com.example.afinal.UserActivity.Fragment

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.example.afinal.R
import com.example.afinal.UserActivity.ApiService
import com.example.afinal.UserActivity.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_account, container, false)

        listView = view.findViewById(R.id.listView)
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1)
        listView.adapter = adapter


        val apiService = RetrofitClient.getClient().create(ApiService::class.java)
        val call = apiService.getManagers()
        call.enqueue(object : Callback<List<Manager>> {
            override fun onResponse(call: Call<List<Manager>>, response: Response<List<Manager>>) =
                if (response.isSuccessful) {
                    val manager = response.body()
                    if (manager != null) {
                        for (managers in manager) {
                            // you can add as many as fields as you want for trial i just added name email & contact
                            adapter.add(managers.name)
                            adapter.add(managers.email)
                            adapter.add(managers.contact_No)
                            adapter.add(managers.state)
                            adapter.add(managers.department)
                        }
                        adapter.notifyDataSetChanged()
                    } else {
                        // error if data is not there
                        Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "API request failed", Toast.LENGTH_SHORT).show()
                }
            override fun onFailure(call: Call<List<Manager>>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
            }
        })

        return view
    }

}
data class Manager(
    val name: String,
    val email: String,
    val contact_No: String,
    val department: String,
    val state: String
)

