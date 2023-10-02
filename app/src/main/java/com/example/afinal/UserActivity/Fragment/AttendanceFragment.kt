package com.example.afinal.UserActivity.Fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
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


    private val ATTENDANCE_STATUS_KEY = "attendance_status"
    private var attendanceStartTime: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_attendance, container, false)


        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("User", null) ?: ""

        val userName = sharedPreferences.getString("UserName", "loading..")


        // Initialize your views
        dateTimeTextView = view.findViewById(R.id.dateTime)
        daymonthTextView = view.findViewById(R.id.dayMonth)
        userStatusTime = view.findViewById(R.id.userTimeOfAttendence)
        presentBtn = view.findViewById(R.id.presentBtn)
        absentBtn = view.findViewById(R.id.absentBtn)
        username = view.findViewById(R.id.Username)
        username.text = " $userName!!"

        chronometer = view.findViewById(R.id.chronometer)
         onsiteIcon = view.findViewById(R.id.onsiteIcon)
         inofficeIcon = view.findViewById(R.id.inofficeIcon)

        val savedStatus = sharedPreferences.getString(ATTENDANCE_STATUS_KEY, "")

        if (savedStatus == "On-Site") {
            updateUI(savedStatus)
        } else {
            updateUI("In-Office")
        }
        val currentDateTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        dateTimeTextView.text = currentDateTime


        val currentDayMonth = SimpleDateFormat("EEEE d,MMM", Locale.getDefault()).format(Date())
        daymonthTextView.text = currentDayMonth

        presentBtn.setOnClickListener {
            if (attendanceStartTime == 0L) {
                // User is marking attendance as "On-Site" for the first time
                attendanceStartTime = System.currentTimeMillis()

                // Start the Chronometer
                chronometer.base = SystemClock.elapsedRealtime()
                chronometer.start()
                chronometer.visibility = View.VISIBLE

                sendAttendanceStatus("On-Site")
                saveAttendanceStatus("On-Site")
            } else {
                // User is marking attendance as "In-Office"
                chronometer.stop()
                chronometer.visibility = View.GONE

                val elapsedTimeMillis = System.currentTimeMillis() - attendanceStartTime
                val overtimeHours = TimeUnit.MILLISECONDS.toHours(elapsedTimeMillis)

                // Check if overtime is greater than 9 hours
                if (overtimeHours > 9) {
                    val overtimeHoursExceeded = overtimeHours - 9
                    // Handle overtime hours exceeding 9 hours (e.g., update UI or database)
                    // You can also send this information to the server
                }

                // Continue marking attendance as "In-Office"
                sendAttendanceStatus("In-Office")
            }
        }



//        presentBtn.setOnClickListener {
//            attendanceStartTime = System.currentTimeMillis()
//            sendAttendanceStatus("On-Site")
//            saveAttendanceStatus("On-Site")
//        }

        absentBtn.setOnClickListener {
            sendAttendanceStatus("In-Office")
        }
        return view
    }
    private fun saveAttendanceStatus(status: String) {
        val editor = sharedPreferences.edit()
        editor.putString(ATTENDANCE_STATUS_KEY, status)
        editor.apply()
    }

    private fun sendAttendanceStatus(status: String) {
        val elapsedTimeMillis = System.currentTimeMillis() - attendanceStartTime
        val overtimeHours = TimeUnit.MILLISECONDS.toHours(elapsedTimeMillis)
        val timer = overtimeHours
        val apiService = RetrofitClient.getClient().create(ApiService::class.java)

        val attendanceData = AttendanceData(userId, status, timer)

        apiService.saveAttendance(attendanceData).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Attendance data saved", Toast.LENGTH_SHORT).show()
                    updateUI(status)
                } else {
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