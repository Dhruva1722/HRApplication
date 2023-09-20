package com.example.afinal.UserActivity


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.afinal.MainActivity
import com.example.afinal.MapActivity.MapsActivity
import com.example.afinal.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


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
            finish()
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


            val loginAdmin = LoginData(email, password)
            val adminDataJson = gson.toJsonTree(loginAdmin).asJsonObject
            Log.d("-----------", "onCreate: user data"+adminDataJson)

            val call = apiService.loginRequest(adminDataJson)
            call.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        Log.d("++++++++++++++", "login response : "+loginResponse)
                        if (loginResponse != null) {
                            val token = loginResponse.token
                            val userId = loginResponse.User

                            // Save the token and userId securely (e.g., using TokenManager)
                            val editor = sharedPreferences.edit()
                            editor.putString("accessToken", token)
                            editor.putString("userId", userId)
                            editor.apply()

                            Log.d("---------", "login token : "+token)
                            Log.d("==========", "login userid : "+ userId)

                            Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            // Handle a missing or invalid response
                            Toast.makeText(this@LoginActivity, "Invalid server response", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Handle specific error codes or messages from the server
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
//                            val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                            Toast.makeText(this@LoginActivity, "Login failed:", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // Handle network errors
                    Toast.makeText(this@LoginActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
//            call.enqueue(object : Callback<Any> {
//                override fun onResponse(call: Call<Any>, response: Response<Any>) {
//                    if (response.isSuccessful) {
//                        val editor = sharedPreferences.edit()
//                        editor.putBoolean("isLoggedIn", true)
//                        editor.apply()
//
//                        Toast.makeText(
//                            this@LoginActivity,
//                            "login Succeccful",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        val intent = Intent(applicationContext, MainActivity::class.java)
//                        startActivity(intent)
//                    } else {
//                        Toast.makeText(
//                            this@LoginActivity,
//                            "login fail",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//
//                override fun onFailure(call: Call<Any>, t: Throwable) {
//                    Toast.makeText(
//                        this@LoginActivity,
//                        "login network error",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            })
        }
    }
}

data class LoginData(
    val email : String,
    val password :String
)
data class LoginResponse(
    @SerializedName("access_token")
    val token: String,
    @SerializedName("user_id")
    val User: String
)