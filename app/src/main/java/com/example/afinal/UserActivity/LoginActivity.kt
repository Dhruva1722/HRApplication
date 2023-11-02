package com.example.afinal.UserActivity


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
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


class LoginActivity : AppCompatActivity() {


    private lateinit var newUserTextView: TextView
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var passResetBtn: TextView

    private val gson = Gson()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Finish the LoginActivity to prevent going back
            return
        }

        emailEditText = findViewById(R.id.loginEmailID)
        passwordEditText = findViewById(R.id.loginPasswordID)
        loginButton = findViewById(R.id.loginBtnID)
        newUserTextView = findViewById(R.id.newUserID)
        passResetBtn = findViewById(R.id.forgetPAsswordID)


        apiService = RetrofitClient.getClient().create(ApiService::class.java)

        newUserTextView.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Perform validation checks
            if (!isValidEmail(email)) {
                emailEditText.error = "Enter a valid email address"
                return@setOnClickListener
            }

            if (password.length < 6) {
                passwordEditText.error = "Password must be at least 6 characters long"
                return@setOnClickListener
            }

            val loginAdmin = LoginData(email, password)
            val adminDataJson = gson.toJsonTree(loginAdmin).asJsonObject
            Log.d("-----------", "onCreate: user data" + adminDataJson)

            val call = apiService.loginRequest(adminDataJson)

            call.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse != null) {
                            val userId = loginResponse.userId // Get the user ID
                            saveUserId(userId)
                            saveUserEmail(email
                            )
                            println("User ID : "+ userId)
                            Log.d("=============", "user id ========" + userId)

                            setLoggedIn(true)
                            Toast.makeText(applicationContext,"login Succeccful",Toast.LENGTH_SHORT).show()
                            // Navigate to the main activity
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                          Toast.makeText(applicationContext,"login fail",Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(applicationContext,"getting error in userid",Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(applicationContext,"network error",Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun saveUserEmail(email: String) {
        val editor = sharedPreferences.edit()
        editor.putString("userEmail", email)
        editor.apply()
    }
    private fun saveUserId(userId: String) {
        val editor = sharedPreferences.edit()
        editor.putString("User", userId)
        editor.apply()
    }

    private fun setLoggedIn(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }
    private fun isValidEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

}
        data class LoginData(val email: String, val password: String)
        data class LoginResponse(val userId: String)








