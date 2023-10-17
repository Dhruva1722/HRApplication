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
    private var isPunchedIn: Boolean = false



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_attendance, container, false)


        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("User", null) ?: ""
        isPunchedIn = sharedPreferences.getBoolean("isPunchedIn", false)


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
            punchIn()
        }
        absentBtn.setOnClickListener {
            punchOut()
        }
        return view
    }

    private fun punchIn() {
        val isPunchIn = true
        savePunchInStatus(isPunchIn)
        setIconVisibility(isPunchIn)


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
    }

    private fun savePunchInStatus(isPunchIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isPunchIn", isPunchIn)
        editor.apply()
    }

    private fun setIconVisibility(isPunchIn: Boolean) {
        if (isPunchIn) {
            onsiteIcon.visibility = View.VISIBLE
//            inofficeIcon.visibility = View.GONE
        } else {
//            onsiteIcon.visibility = View.GONE
            inofficeIcon.visibility = View.VISIBLE
        }
    }
}


data class AttendanceData(
    val userId: String,
    val isPunchIn: Boolean
)



