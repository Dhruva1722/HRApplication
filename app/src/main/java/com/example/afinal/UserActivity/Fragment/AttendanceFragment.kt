package com.example.afinal.UserActivity.Fragment

import android.app.ProgressDialog.show
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.afinal.R
import com.example.afinal.UserActivity.ApiService
import com.example.afinal.UserActivity.RetrofitClient
//import com.example.afinal.UserActivity.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit




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

    private lateinit var chronometer: Chronometer
    private var isChronometerRunning = false

    private val BUTTON_STATE_KEY = "button_state"

    private val DATE_KEY = "date"

    private val ATTENDANCE_STATUS_KEY = "attendance_status"
    private var attendanceStartTime: Long = 0L
    private val CHRONOMETER_STATE_KEY = "chronometer_state"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_attendance, container, false)


        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("User", null) ?: ""


        val storedDate = sharedPreferences.getString(DATE_KEY, "")
        val chronometerState = sharedPreferences.getLong(CHRONOMETER_STATE_KEY, 0L)

        // Initialize your views
        dateTimeTextView = view.findViewById(R.id.dateTime)
        daymonthTextView = view.findViewById(R.id.dayMonth)
        userStatusTime = view.findViewById(R.id.userTimeOfAttendence)
        presentBtn = view.findViewById(R.id.presentBtn)
        absentBtn = view.findViewById(R.id.absentBtn)
        username = view.findViewById(R.id.Username)

        val userEmail = sharedPreferences.getString("userEmail", "")
        val parts = userEmail?.split("@")
        if (parts?.size == 2) {
            val name = parts[0]
            username.text = "Hello, $name!!"
        }

        chronometer = view.findViewById(R.id.chronometer)
        onsiteIcon = view.findViewById(R.id.onsiteIcon)
        inofficeIcon = view.findViewById(R.id.inofficeIcon)

        val savedStatus = sharedPreferences.getString(ATTENDANCE_STATUS_KEY, "")

        val DateTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())

        if (savedStatus == "On-Site") {
            updateUI(savedStatus)
        } else {
            updateUI("In-Office")
        }
        val currentDateTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        dateTimeTextView.text = currentDateTime


        val currentDayMonth = SimpleDateFormat("EEEE d,MMM", Locale.getDefault()).format(Date())
        daymonthTextView.text = currentDayMonth

        val isButtonPressed = sharedPreferences.getBoolean(BUTTON_STATE_KEY, false)
        if (isButtonPressed) {
            val chronometerBase = sharedPreferences.getLong(CHRONOMETER_STATE_KEY, 0L)
            if (chronometerBase > 0L) {
                val elapsedTime = SystemClock.elapsedRealtime() - chronometerBase
                chronometer.base = SystemClock.elapsedRealtime() - elapsedTime
                chronometer.start()
                chronometer.visibility = View.VISIBLE
                presentBtn.isEnabled = false
                presentBtn.setBackgroundResource(R.drawable.btn_disable_bg)
            }
        }

        presentBtn.setOnClickListener {
            if (attendanceStartTime == 0L) {
                attendanceStartTime = System.currentTimeMillis()
                chronometer.base = SystemClock.elapsedRealtime()
                chronometer.start()
                chronometer.visibility = View.VISIBLE

                sendAttendanceStatus("On-Site")
                saveAttendanceStatus("On-Site")

                presentBtn.isEnabled = false
                presentBtn.setBackgroundResource(R.drawable.btn_disable_bg)
                val editor = sharedPreferences.edit()
                editor.putBoolean(BUTTON_STATE_KEY, true)
                editor.putString(DATE_KEY, DateTime)
                editor.putLong(CHRONOMETER_STATE_KEY, chronometer.base)
                editor.apply()
            } else {
                val elapsedTimeMillis = System.currentTimeMillis() - attendanceStartTime
                val overtimeHours = TimeUnit.MILLISECONDS.toHours(elapsedTimeMillis)
                if (overtimeHours > 9) {
                    val overtimeHoursExceeded = overtimeHours - 9
                    val toastMessage = "You have worked $overtimeHoursExceeded hours overtime."
                    Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        absentBtn.setOnClickListener {
            chronometer.stop()
            chronometer.visibility = View.GONE
            isChronometerRunning = false

            val editor = sharedPreferences.edit()
            editor.remove(CHRONOMETER_STATE_KEY)
            editor.apply()
            sendAttendanceStatus("In-Office")
            saveAttendanceStatus("In-Office")
        }
        return view
    }
    private fun saveAttendanceStatus(status: String) {
        val editor = sharedPreferences.edit()
        editor.putString(ATTENDANCE_STATUS_KEY, status)
        editor.apply()
    }
    private fun sendAttendanceStatus(status: String) {
        val currentTimeMillis = System.currentTimeMillis()
        val elapsedTimeMillis = currentTimeMillis - attendanceStartTime
        val workingHours = TimeUnit.MILLISECONDS.toHours(elapsedTimeMillis)
        val timer = workingHours
        val apiService = RetrofitClient.getClient().create(ApiService::class.java)

        val attendanceData = AttendanceData(userId,status,timer)
        Log.d("-----------", "sendAttendanceStatus: "+ attendanceData)

        apiService.saveAttendance(attendanceData).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Attendance data saved", Toast.LENGTH_SHORT).show()
                    updateUI(status)
                } else {
                    Log.e("API Error", "Response Code: ${response.code()}, Message: ${response.message()}")
                    Toast.makeText(requireContext(), "Failed to save attendance data", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
            }
        })
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
    val Emp_status: String,
    val timer: Long
)