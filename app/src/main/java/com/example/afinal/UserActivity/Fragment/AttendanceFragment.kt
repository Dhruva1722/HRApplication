package com.example.afinal.UserActivity.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
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



class AttendanceFragment : Fragment() {

    private lateinit var dateTimeTextView: TextView
    private lateinit var username: TextView
    private lateinit var userStatusTime: TextView
    private lateinit var onlineOfflineBtn: ImageView
    private lateinit var presentBtn: Button
    private lateinit var absentBtn: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private var isPresent = false
    private var attendanceTime: String? = null


    private var lastPresentTime: Long = 0
    private val workingStartTime = 10 // 10:00 AM
    private val workingEndTime = 19 // 7:00 PM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_attendance, container, false)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Initialize your views
        dateTimeTextView = view.findViewById(R.id.dateTime)
        userStatusTime = view.findViewById(R.id.userTimeOfAttendence)
        onlineOfflineBtn = view.findViewById(R.id.onlineOfflineBtn)
        presentBtn = view.findViewById(R.id.presentBtn)
        absentBtn = view.findViewById(R.id.absentBtn)
        username = view.findViewById(R.id.Username)


        fetchAndDisplayUsername()


        val currentDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        dateTimeTextView.text = currentDateTime


        presentBtn.setOnClickListener {
            setAttendance(true)
        }

        absentBtn.setOnClickListener {
            setAttendance(false)
        }

        if (savedInstanceState != null) {
            isPresent = savedInstanceState.getBoolean("isPresent")
            attendanceTime = savedInstanceState.getString("attendanceTime")
            updateUI()
        }

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
                        usernameTextView?.text = " $username"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error if needed
                    Toast.makeText(context, "Getting error fetching username", Toast.LENGTH_SHORT).show()
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
        val currentTime = SimpleDateFormat("HH:mm:ss").format(Date())
        val currentHour = SimpleDateFormat("HH").format(Date()).toInt()
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            val database = FirebaseDatabase.getInstance()
            val userAttendanceRef = database.getReference("user_attendance").child(userId)

            if (currentHour in workingStartTime until workingEndTime) {
                if (present) {
                    val timeInMillis = System.currentTimeMillis()
                    if (timeInMillis - lastPresentTime > 3600000) {
                        // More than 1 hour has passed since the last "Present" press
                        lastPresentTime = timeInMillis
                        onlineOfflineBtn.setImageResource(R.drawable.onlinebtn)
                        isPresent = true
                        attendanceTime = currentTime
                        val attendanceRecord = "Present at $currentTime"
                        val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
                        val attendanceDateRef = userAttendanceRef.child(currentDate)
                        val newAttendanceRef = attendanceDateRef.push()
                        newAttendanceRef.setValue(attendanceRecord)
                        updateUI()
                    } else {
                        // Less than 1 hour has passed since the last "Present" press
                        Toast.makeText(context, "You can only mark Present once per hour", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    onlineOfflineBtn.setImageResource(R.drawable.offlinebtn)
                    isPresent = false
                    attendanceTime = currentTime
                    val attendanceRecord = "Absent at $currentTime"
                    val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
                    val attendanceDateRef = userAttendanceRef.child(currentDate)
                    val newAttendanceRef = attendanceDateRef.push()
                    newAttendanceRef.setValue(attendanceRecord)
                    updateUI()
                }
            } else {
                // Outside working hours
                userStatusTime.text = "You are outside working hours."
            }

            // Check for overtime at 7 o'clock
            if (currentHour == workingEndTime && isPresent) {
                userStatusTime.text = "Your time is over."
            }
        }
    }

//    private fun setAttendance(present: Boolean) {
//        val currentTime = SimpleDateFormat("HH:mm:ss").format(Date())
//        val currentHour = SimpleDateFormat("HH").format(Date()).toInt()
//        val userId = mAuth.currentUser?.uid
//        if (userId != null) {
//            val database = FirebaseDatabase.getInstance()
//            val userAttendanceRef = database.getReference("user_attendance").child(userId)
//
//            if (currentHour >= 10 && currentHour < 19) {
//                val attendanceRecord = if (present) {
//                    "Present at $currentTime"
//                } else {
//                    "Absent at $currentTime"
//                }
//
//                val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
//
//                // Push a new attendance record with a unique key under the current date
//                val newAttendanceRef = userAttendanceRef.child(currentDate).push()
//                newAttendanceRef.setValue(attendanceRecord)
//
//                onlineOfflineBtn.setImageResource(if (present) R.drawable.onlinebtn else R.drawable.offlinebtn)
//                isPresent = present
//                attendanceTime = currentTime
//                updateUI()
//            } else {
//                // Outside working hours
//                userStatusTime.text = "You are outside working hours."
//            }
//
//            // Check for overtime
//            if (currentHour >= 19 && isPresent) {
//                val overTime = currentHour - 19
//                userStatusTime.text = "You have $overTime hours of overtime."
//            }
//        }

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