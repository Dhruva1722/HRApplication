package com.example.afinal.UserActivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.afinal.MainActivity
import com.example.afinal.R
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegistrationActivity : AppCompatActivity() {

    private lateinit var haveAnAccount: TextView

    private val gson = Gson()
    private lateinit var apiService: ApiService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        apiService = RetrofitClient.getClient().create(ApiService::class.java)

        haveAnAccount = findViewById(R.id.haveAnAccountTxt)
        haveAnAccount.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        val registerButton = findViewById<Button>(R.id.registerBtn)
        val registerEmpID = findViewById<TextInputEditText>(R.id.registerID)
        val registerName = findViewById<TextInputEditText>(R.id.registerNameID)
        val registerEmail = findViewById<TextInputEditText>(R.id.registerEmailID)
        val registerPassword = findViewById<TextInputEditText>(R.id.registerPasswordID)
        val registerConfirmPass = findViewById<TextInputEditText>(R.id.confirm_pass_ID)

        registerButton.setOnClickListener {
            val Emp_ID = registerEmpID.text.toString()
            val name = registerName.text.toString()
            val email = registerEmail.text.toString()
            val password = registerPassword.text.toString()
            val confirm_password = registerConfirmPass.text.toString()

            val registrationData = RegistrationData(Emp_ID,name, email, password, confirm_password)
            val registrationDataJson = gson.toJsonTree(registrationData).asJsonObject
            Log.d("-----------", "onCreate: user data"+registrationDataJson)
            // Make the API request
            val call = apiService.registerUser(registrationDataJson)

            call.enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@RegistrationActivity,
                            "Registration Succeccful",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@RegistrationActivity,
                            "Registration fail",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Registration network error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

    }
}

data class RegistrationData(
    val Emp_ID:String,
    val name: String,
    val email: String,
    val password: String,
    val confirm_password: String
)
