package com.example.afinal.UserActivity.Fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.afinal.R
import com.example.afinal.UserActivity.ApiService
import com.example.afinal.UserActivity.RetrofitClient
//import com.example.afinal.UserActivity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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
    private lateinit var onlineOfflineBtn: ImageView
    private lateinit var presentBtn: LinearLayout
    private lateinit var absentBtn: LinearLayout

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userId: String


    private var isPresent = false
    private var attendanceTime: String? = null




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_attendance, container, false)


        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("User", null) ?: ""

        val userName = sharedPreferences.getString("UserName", "loading....")


        // Initialize your views
        dateTimeTextView = view.findViewById(R.id.dateTime)
        daymonthTextView = view.findViewById(R.id.dayMonth)
        userStatusTime = view.findViewById(R.id.userTimeOfAttendence)
        onlineOfflineBtn = view.findViewById(R.id.onlineOfflineBtn)
        presentBtn = view.findViewById(R.id.presentBtn)
        absentBtn = view.findViewById(R.id.absentBtn)
        username = view.findViewById(R.id.Username)
        username.text = " Hello $userName!!"

        val chronometer = view.findViewById<Chronometer>(R.id.txtTime)
        chronometer.setOnChronometerTickListener {
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

            if (currentTime >= "7:15") {
                userStatusTime.text = "You have overtime."
            }
        }


        val currentDateTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        dateTimeTextView.text = currentDateTime


        val currentDayMonth = SimpleDateFormat("EEEE d,MMM", Locale.getDefault()).format(Date())
        daymonthTextView.text = currentDayMonth

        presentBtn.setOnClickListener {
            chronometer.base = SystemClock.elapsedRealtime()
            chronometer.start()
            sendAttendanceStatus("Present")
        }

        absentBtn.setOnClickListener {
            sendAttendanceStatus("Absent")
        }
        return view
    }

    private fun sendAttendanceStatus(status: String) {

        val apiService = RetrofitClient.getClient().create(ApiService::class.java)

        val attendanceData = AttendanceData(userId, status)

        apiService.saveAttendance(attendanceData).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(activity, "Attendance data saved", Toast.LENGTH_SHORT).show()
                    updateUI(status)
                } else {
                    Toast.makeText(activity, "Failed to save attendance data", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(activity, "Network error", Toast.LENGTH_SHORT).show()
            }

        })

    }
    private fun updateUI(status: String) {
        val statusText = if (status == "Present") "Present" else "Absent"
        val message = "You marked $statusText at ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())}."
        userStatusTime.text = message
    }
}
data class AttendanceData(
    val userId: String,
    val Emp_status: String
)