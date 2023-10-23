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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil.setContentView
import com.example.afinal.R
import com.example.afinal.UserActivity.Adapter.LeaveAdapter
import com.example.afinal.UserActivity.ApiService
import com.example.afinal.UserActivity.RetrofitClient
//import com.example.afinal.UserActivity.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AttendanceFragment : Fragment() {

    private lateinit var dateTimeTextView: TextView
    private lateinit var daymonthTextView: TextView
    private lateinit var username: TextView
    private lateinit var userStatusTime: TextView
    private lateinit var onsiteIcon: ImageView
    private lateinit var inofficeIcon: ImageView
    private lateinit var presentBtn: LinearLayout
    private lateinit var absentBtn: LinearLayout

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userId: String
    private var isPunchedIn: Boolean = false
    private var isDateChanged: Boolean = false

    private val ATTENDANCE_STATUS_KEY = "attendance_status"
    private val LAST_ATTENDANCE_DATE_KEY = "last_attendance_date"

    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_attendance, container, false)


        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("User", null) ?: ""
        isPunchedIn = sharedPreferences.getBoolean("isPunchedIn", false)

        val lastAttendanceDate = sharedPreferences.getString(LAST_ATTENDANCE_DATE_KEY, "")


        dateTimeTextView = view.findViewById(R.id.dateTime)
        daymonthTextView = view.findViewById(R.id.dayMonth)
        userStatusTime = view.findViewById(R.id.userTimeOfAttendence)
        presentBtn = view.findViewById(R.id.presentBtn)
        absentBtn = view.findViewById(R.id.absentBtn)
        username = view.findViewById(R.id.Username)

        val listView = view.findViewById<ListView>(R.id.getLeaveStatus)


        val userEmail = sharedPreferences.getString("userEmail", "")
        val parts = userEmail?.split("@")
        if (parts?.size == 2) {
            val name = parts[0]
            username.text = "Hello, $name!!"
        }

        onsiteIcon = view.findViewById(R.id.onsiteIcon)
        inofficeIcon = view.findViewById(R.id.inofficeIcon)

        val currentDateTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        dateTimeTextView.text = currentDateTime


        val currentDayMonth = SimpleDateFormat("EEEE d,MMM", Locale.getDefault()).format(Date())
        daymonthTextView.text = currentDayMonth

        val savedStatus = sharedPreferences.getString(ATTENDANCE_STATUS_KEY, "")
        if (savedStatus == "On-Site") {
            updateUI(savedStatus)
        } else {
            updateUI("In-Office")
            updateUI(savedStatus ?: "")
        }

        isDateChanged = hasDateChanged(lastAttendanceDate)

        if (isPunchedIn) {
            if (isDateChanged) {
                presentBtn.isEnabled = true
                absentBtn.isEnabled = true
            } else {
                presentBtn.isEnabled = false
                absentBtn.isEnabled = false
            }
        }

        presentBtn.setOnClickListener {
            punchIn()
        }
        absentBtn.setOnClickListener {
            punchOut()
        }

        val apiService = RetrofitClient.getClient().create(ApiService::class.java)
            val call = apiService.getLeaveData()
            call.enqueue(object : Callback<List<Leave>> {
                override fun onResponse(call: Call<List<Leave>>, response: Response<List<Leave>>) {
                    if (response.isSuccessful) {
                        val leaveDataList = response.body()
                        if (leaveDataList != null) {

                            val leaveItems = leaveDataList.map {
                                "Start Date: ${it.startDate}, End Date: ${it.endDate}, Status: ${it.status}, Days: ${it.numberOfDays}"
                            }.toTypedArray()

                            Log.d("=============", "onResponse: leaveData ${leaveItems}")
                            // Create an adapter and set it to the ListView
                            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, leaveItems)
                            listView.adapter = adapter
                            Toast.makeText(requireContext(), " successfully", Toast.LENGTH_SHORT).show()

                        }
                    }
                }

                override fun onFailure(call: Call<List<Leave>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT).show()
                }
            })


        return view
    }
    private fun hasDateChanged(lastAttendanceDate: String?): Boolean {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return currentDate != lastAttendanceDate
    }

    private fun punchIn() {
        val isPunchIn = true

        val apiService = RetrofitClient.getClient().create(ApiService::class.java)

        val attendanceData = AttendanceData(userId, isPunchIn)


        val call = apiService.saveAttendance(attendanceData)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Punched in successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to punch in", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
            }
        })
        saveAttendanceStatus("On-Site")
        saveLastAttendanceDate()

    }

    private fun punchOut() {
        val isPunchIn = false

        val apiService = RetrofitClient.getClient().create(ApiService::class.java)

        val attendanceData = AttendanceData(userId, isPunchIn)


        val call = apiService.saveAttendance(attendanceData)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Punched out successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to punch out", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
            }
        })
        saveAttendanceStatus("In-Office")
        saveLastAttendanceDate()
    }
    private fun saveLastAttendanceDate() {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val editor = sharedPreferences.edit()
        editor.putString(LAST_ATTENDANCE_DATE_KEY, currentDate)
        editor.apply()
    }

    private fun saveAttendanceStatus(status: String) {
        val editor = sharedPreferences.edit()
        editor.putString(ATTENDANCE_STATUS_KEY, status)
        editor.apply()
        updateUI(status)
    }

    private fun updateUI(status: String) {
        val statusText = if (status == "On-Site") {
            onsiteIcon.visibility = View.VISIBLE
            inofficeIcon.visibility = View.GONE
            "On-Site"
        } else {
            onsiteIcon.visibility = View.GONE
            inofficeIcon.visibility = View.VISIBLE
            "In-Office"
        }
        val message =
            "You marked $statusText at ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())}."
        userStatusTime.text = message
    }

}

data class AttendanceData(
    val userId: String,
    val isPunchIn: Boolean
)

data class Leave(
    val startDate: String,
    val endDate: String,
    val status: String,
    val numberOfDays: Int
)


