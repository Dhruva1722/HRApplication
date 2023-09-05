package com.example.afinal.UserActivity.Fragment

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
import com.example.afinal.UserActivity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private var isPresent = false
    private var attendanceTime: String? = null

    private var presentButtonPressTime: Long = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_attendance, container, false)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Initialize your views
        dateTimeTextView = view.findViewById(R.id.dateTime)
        daymonthTextView = view.findViewById(R.id.dayMonth)
        userStatusTime = view.findViewById(R.id.userTimeOfAttendence)
        onlineOfflineBtn = view.findViewById(R.id.onlineOfflineBtn)
        presentBtn = view.findViewById(R.id.presentBtn)
        absentBtn = view.findViewById(R.id.absentBtn)
        username = view.findViewById(R.id.Username)

        val chronometer = view.findViewById<Chronometer>(R.id.txtTime)
        chronometer.setOnChronometerTickListener {
            // Check the time when the Chronometer ticks
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

            if (currentTime >= "7:15") {
                // 7:00 PM or later, show overtime message
                userStatusTime.text = "You have overtime."
            }
        }


        fetchAndDisplayUsername()


        val currentDateTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        dateTimeTextView.text = currentDateTime


        val currentDayMonth = SimpleDateFormat("EEEE d,MMM", Locale.getDefault()).format(Date())
        daymonthTextView.text = currentDayMonth

        presentBtn.setOnClickListener {
            chronometer.base = SystemClock.elapsedRealtime() // Reset the Chronometer
            chronometer.start()
            setAttendance(true)
        }

        absentBtn.setOnClickListener {
            setAttendance(false)
        }

//        if (savedInstanceState != null) {
//            isPresent = savedInstanceState.getBoolean("isPresent")
//            attendanceTime = savedInstanceState.getString("attendanceTime")
//            updateUI()
//        }


        return view
    }
    private fun fetchAndDisplayUsername() {
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            val userRef = database.getReference("users").child(userId)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username = snapshot.child("name").getValue(String::class.java)
                    if (username != null) {
                        val usernameTextView = view?.findViewById<TextView>(R.id.Username)
                        usernameTextView?.text = "Hii $username !"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error if needed
                    Toast.makeText(context, "Getting error fetching username please check your internet connection...", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//
//        // Save the state
//        outState.putBoolean("isPresent", isPresent)
//        outState.putString("attendanceTime", attendanceTime)
//    }

//    override fun onResume() {
//        super.onResume()
//
//        // Check if there is saved state, and if so, restore it
//        if (isPresent) {
//            onlineOfflineBtn.setImageResource(R.drawable.onlinebtn)
//        } else {
//            onlineOfflineBtn.setImageResource(R.drawable.offlinebtn)
//        }
//        updateUI()
//    }



    private fun setAttendance(present: Boolean) {
        val currentTime = SimpleDateFormat("dd-mm-yy hh:mm ", Locale.getDefault()).format(Date())
        val currentHour = SimpleDateFormat("HH").format(Date()).toInt()
        val userId = mAuth.currentUser?.uid

        if (userId != null) {
            val database = FirebaseDatabase.getInstance()
            val userAttendanceRef = database.getReference("users").child(userId).child("attendance")
            val statusRef = database.getReference("users").child(userId).child("status")

            if (currentHour >= 10 && currentHour < 19) {
                if (present) {
                    // Check if the user is already marked as Present
                    if (!isPresent) {
                        // Update UI, set the status as Present, and record the attendance
                        onlineOfflineBtn.setImageResource(R.drawable.onlinebtn)
                        isPresent = true
                        attendanceTime = currentTime
                        statusRef.setValue("Present")
                        val newAttendanceRef = userAttendanceRef.push()
                        newAttendanceRef.setValue("Present at $currentTime")
                        updateUI()
                    }
                } else {
                    // If marking as Absent, update UI, set the status as Absent, and record the attendance
                    onlineOfflineBtn.setImageResource(R.drawable.offlinebtn)
                    isPresent = false
                    attendanceTime = currentTime
                    statusRef.setValue("Absent")
                    val newAttendanceRef = userAttendanceRef.push()
                    newAttendanceRef.setValue("Absent at $currentTime")
                    updateUI()
                }
            } else {
                // Outside working hours
                userStatusTime.text = "You are outside working hours."
            }

            // Check for overtime
            if (currentHour >= 19 && isPresent) {
                val overTime = currentHour - 19
                userStatusTime.text = "You have $overTime hours of overtime."
            }
        }
    }


    private fun updateUI() {
        val statusText = if (isPresent) "Present" else "Absent"
        val message = if (attendanceTime != null) {
            "You marked $statusText at $attendanceTime."
        } else {
            "You are $statusText."
        }

        userStatusTime.text = message
    }

}