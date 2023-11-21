package com.example.afinal.UserActivity

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.afinal.R
import com.example.afinal.UserActivity.Adapter.LeaveAdapter
import com.google.android.material.textfield.TextInputLayout
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class LeaveStatus : AppCompatActivity() {

    private lateinit var applyBtn : Button
    private lateinit var leaveadapter: ArrayAdapter<String>

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userId: String

    val apiService = RetrofitClient.getClient().create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leave_status)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("User", null) ?: ""


        val listOfLeave = findViewById<ListView>(R.id.leaveStatusList)
        leaveadapter = ArrayAdapter(this@LeaveStatus, R.layout.leavestatus)
        listOfLeave.adapter = leaveadapter

        fetchLeaveApplications(userId)
        applyBtn = findViewById(R.id.applyForLeaveBtn)

        applyBtn.setOnClickListener {
            LeaveApplicationDialog()
        }


    }
        private fun LeaveApplicationDialog() {

            val builder = AlertDialog.Builder(applicationContext)

            val view = layoutInflater.inflate(R.layout.leave_item, null)

            val textStartDate = view.findViewById<TextInputLayout>(R.id.textStartDate)
            val textEndDate = view.findViewById<TextInputLayout>(R.id.textEndDate)
            val applyBtn = view.findViewById<Button>(R.id.applyBtn)


            applyBtn.setOnClickListener {

                val startDateStr = textStartDate.editText!!.text.toString()
                val endDateStr = textEndDate.editText!!.text.toString()

                Log.d("LeaveApplicationDialog", "startDateStr: $startDateStr")
                Log.d("LeaveApplicationDialog", "endDateStr: $endDateStr")

                if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
                    Toast.makeText(
                        this@LeaveStatus,
                        "Please fill in all fields",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val startDate = convertToUnixTimestamp(startDateStr)
                val endDate = convertToUnixTimestamp(endDateStr)

                Log.d("LeaveApplicationDialog", "startDate: $startDate")
                Log.d("LeaveApplicationDialog", "endDate: $endDate")

                if (startDate != null && endDate != null) {

                    val startDate = convertToISO8601(startDate)
                    val endDate = convertToISO8601(endDate)

                    val leaveRequest = LeaveRequest(startDate, endDate)
                    Log.d("LeaveApplicationDialog", "LeaveRequest: $leaveRequest")
                    val call = apiService.postLeaveRequest(userId, leaveRequest)

                    call.enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                   this@LeaveStatus,
                                    "Leave request submitted successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@LeaveStatus,
                                    "Failed to submit leave request",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Toast.makeText(this@LeaveStatus, "Network error", Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
                } else {
                    Toast.makeText(this@LeaveStatus, "Invalid date format", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            builder.setView(view)

            val dialog = builder.create()
            dialog.show()
        }




    private fun convertToUnixTimestamp(dateStr: String): Long? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return try {
            val date = dateFormat.parse(dateStr)
            date?.time
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

    private fun convertToISO8601(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.format(Date(timestamp))
    }

    private fun fetchLeaveApplications(userId: String) {
        val call = apiService.getLeaveApplications(userId)
        call.enqueue(object : Callback<LeaveResponse> {
            override fun onResponse(call: Call<LeaveResponse>, response: Response<LeaveResponse>) {
                if (response.isSuccessful) {
                    val leaveResponse = response.body()
                    if (leaveResponse != null) {
                        val totalNumberOfDays = leaveResponse.totalNumberOfDays
                        val leaveApplications = leaveResponse.leaveApplications

                        val totalNumberOfDaysTextView =findViewById<TextView>(R.id.leaveBalanceTv)
                        totalNumberOfDaysTextView.text = "Total Number of Days: $totalNumberOfDays"

                        val leaveListView = findViewById<ListView>(R.id.leaveStatusList)
                        val leaveAdapter = LeaveAdapter(this@LeaveStatus, leaveApplications)
                        leaveListView.adapter = leaveAdapter
                    } else {
                        Toast.makeText(this@LeaveStatus, "No data available", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        404 -> "No Data Available for Leave"
                        else -> "API request failed"
                    }
                    Toast.makeText(this@LeaveStatus, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LeaveResponse>, t: Throwable) {
                Log.d("*****************", "Network error: ${t.message}")
                Toast.makeText(this@LeaveStatus, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}



data class LeaveRequest(
    val startDate: String,
    val endDate: String
)

data class LeaveResponse(
    val totalNumberOfDays: Int,
    val leaveApplications: List<LeaveInfo>
)

data class LeaveInfo(
    val startDate: String,
    val endDate: String,
    val status: String,
    val numberOfDays: Int
)