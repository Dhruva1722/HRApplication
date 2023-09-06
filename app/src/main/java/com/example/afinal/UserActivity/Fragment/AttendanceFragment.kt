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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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

    private lateinit var db: FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_attendance, container, false)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        db = FirebaseFirestore.getInstance()

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
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

            if (currentTime >= "7:15") {
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
        return view
    }
    private fun fetchAndDisplayUsername() {
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            val userDocumentRef = db.collection("users").document(userId)

            userDocumentRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val username = documentSnapshot.getString("name")
                        if (username != null) {
                            val usernameTextView = view?.findViewById<TextView>(R.id.Username)
                            usernameTextView?.text = "Hii, $username!"
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Handle the error if needed
                    Toast.makeText(
                        context,
                        "Error fetching username: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun setAttendance(present: Boolean) {
        val currentTime = SimpleDateFormat("dd-MM-yy HH:mm", Locale.getDefault()).format(Date())
        val currentHour = SimpleDateFormat("HH", Locale.getDefault()).format(Date()).toInt()
        val userId = mAuth.currentUser?.uid

        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val userDocumentRef = db.collection("users").document(userId)

            if (currentHour >= 10 && currentHour < 19) {
                if (present) {
                    if (!isPresent) {
                        onlineOfflineBtn.setImageResource(R.drawable.onlinebtn)
                        isPresent = true
                        attendanceTime = currentTime
                        userDocumentRef.update("status", "Present")

                        // Save attendance data to Firestore
                        val attendanceData = hashMapOf(
                            "attendanceType" to "Present",
                            "timestamp" to FieldValue.serverTimestamp()
                        )
                        userDocumentRef.collection("attendance").add(attendanceData)

                        updateUI()
                    }
                } else {
                    onlineOfflineBtn.setImageResource(R.drawable.offlinebtn)
                    isPresent = false
                    attendanceTime = currentTime
                    userDocumentRef.update("status", "Absent")

                    val attendanceData = hashMapOf(
                        "attendanceType" to "Absent",
                        "timestamp" to FieldValue.serverTimestamp()
                    )
                    userDocumentRef.collection("attendance").add(attendanceData)

                    updateUI()
                }
            } else {
                userStatusTime.text = "You are outside working hours."
            }
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