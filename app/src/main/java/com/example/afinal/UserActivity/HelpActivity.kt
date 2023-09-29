package com.example.afinal.UserActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.example.afinal.R
import com.example.afinal.UserActivity.Adapter.ManagerAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HelpActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)


        listView = findViewById(R.id.listView)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        listView.adapter = adapter

        val apiService = RetrofitClient.getClient().create(ApiService::class.java)
        val call = apiService.getManagers()
        call.enqueue(object : Callback<List<Manager>> {
            override fun onResponse(call: Call<List<Manager>>, response: Response<List<Manager>>) {
                if (response.isSuccessful) {
                    val managers = response.body()
                    if (managers != null) {
                        val adapter = ManagerAdapter(this@HelpActivity, R.layout.manager_list, managers)
                        listView.adapter = adapter
                    } else {
                        Toast.makeText(this@HelpActivity, "No data available", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@HelpActivity, "API request failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Manager>>, t: Throwable) {
                Toast.makeText(this@HelpActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

data class Manager(
    val name: String,
    val email: String,
    val contact_no: String,
    val department:String
)



