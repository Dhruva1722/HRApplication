package com.example.afinal.UserActivity

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.afinal.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class ComplaintActivity : AppCompatActivity() {

    private lateinit var  edtMsgInput: TextInputEditText
    private lateinit var submitButton: Button

    private lateinit var sharedPreferences: SharedPreferences
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complaint)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("User", null) ?: ""



        edtMsgInput = findViewById(R.id.msg_input)
        submitButton = findViewById(R.id.submit_btn)


        submitButton.setOnClickListener {
            submitComplaint();
        }

    }

    private fun submitComplaint() {

        val complaintMessage = edtMsgInput.text.toString()

        if (complaintMessage.isNotEmpty() && userId != null) {
            val apiService = RetrofitClient.getClient().create(ApiService::class.java)


            val requestBody = ComplaintRequest(userId!!, complaintMessage)
            Log.d("==================", "submitComplaint: employee complain  ${requestBody}")
            apiService.submitComplaint(requestBody).enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    if (response.isSuccessful) {
                        Toast.makeText(applicationContext, "Complaint submitted successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(applicationContext, "Failed to submit complaint", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Toast.makeText(applicationContext, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(applicationContext, "Please enter a complaint message", Toast.LENGTH_SHORT).show()
        }

    }

}
data class ComplaintRequest(
    val userId: String,
    val message: String
)


