package com.example.afinal.UserActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.afinal.R
import com.example.afinal.UserActivity.Adapter.EmployeeAdapter
import kotlinx.coroutines.launch

class HelpActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val employeeAdapter = EmployeeAdapter()
    val apiService = RetrofitClient.getClient().create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        val recyclerView: RecyclerView = findViewById(R.id.listView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = employeeAdapter

        fetchManagers()
    }

    private fun fetchManagers() {
        lifecycleScope.launch {
            try {
                val users = apiService.getManagers()
//                Log.d("ApiResponse", users.toString())

                // Filter users with designation containing "Manager" (case-insensitive)
                val managerList: List<Employee> = users.filter { it.Emp_designation.contains("Manager", ignoreCase = true) }
                Log.d("ApiResponse", "${managerList}")
                // Update the RecyclerView with the list of managers
                employeeAdapter.submitList(managerList)
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }
}

data class Employee(
    val Emp_contact_No: String,
    val Emp_name: String,
    val Emp_designation: String,
)



