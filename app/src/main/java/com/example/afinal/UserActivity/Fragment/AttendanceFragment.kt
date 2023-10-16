package com.example.afinal.UserActivity.Fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


    private val BUTTON_STATE_KEY = "button_state"

    private val DATE_KEY = "date"

    private val ATTENDANCE_STATUS_KEY = "attendance_status"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_attendance, container, false)


        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("User", null) ?: ""


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

        onsiteIcon = view.findViewById(R.id.onsiteIcon)
        inofficeIcon = view.findViewById(R.id.inofficeIcon)


        val currentDateTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        dateTimeTextView.text = currentDateTime


        val currentDayMonth = SimpleDateFormat("EEEE d,MMM", Locale.getDefault()).format(Date())
        daymonthTextView.text = currentDayMonth


        presentBtn.setOnClickListener {


            onsiteIcon.visibility = View.VISIBLE
            inofficeIcon.visibility = View.GONE

            markAttendance(action = "Punch In", empStatus = "onsite")
        }
        absentBtn.setOnClickListener {

            onsiteIcon.visibility = View.GONE
            inofficeIcon.visibility = View.VISIBLE

            markAttendance(action = "Punch Out", empStatus = "inoffice")
        }
        return view
    }

    fun isValidAction(action: String): Boolean {
        return action == ActionConstants.PUNCH_IN || action == ActionConstants.PUNCH_OUT
    }
    private fun markAttendance(action: String,empStatus:String) {


        if (isValidAction(action)) {
            val attendanceData = AttendanceData(userId, action, empStatus)

            val apiService = RetrofitClient.getClient().create(ApiService::class.java)

            val call = apiService.saveAttendance(attendanceData)
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                        Log.d("API Success", "Response successful")
                    } else {
                        Toast.makeText(requireContext(), "Fail", Toast.LENGTH_SHORT).show()
                        Log.d("API fail", "Response successful")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
                    Log.d("API Error", "Response successful")
                }
            })
        } else {
            Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
            Log.d("API Success", "Response successful")
    }


    }
}
    object ActionConstants {
        const val PUNCH_IN = "Punch In"
        const val PUNCH_OUT = "Punch Out"
    }

    data class AttendanceData(
        val userId: String,
        val action: String,
        val Emp_status: String
    )