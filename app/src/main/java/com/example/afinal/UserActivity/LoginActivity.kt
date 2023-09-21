package com.example.afinal.UserActivity


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.afinal.MainActivity
import com.example.afinal.R
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



private val Any.User: String?
    get() {return null}
private val Any.token: String?
    get() {return null}

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

        val tokenManager = TokenManager(this)
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

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
                            // Save the JWT token to SharedPreferences or a secure storage method
                            val token = loginResponse.token
                            tokenManager.saveToken(token)

                            Log.d("-----------", "onCreate: user data========" + token)

                            // Navigate to the main activity
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish() // Close the login activity
                        } else {
                            // Handle null response body
                        }
                    } else {
                        // Handle non-successful response (e.g., login failed)
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // Handle network or other errors
                }
            })
        }
    }

    class TokenManager(private val context: Context) {
        private val preferences: SharedPreferences =
            context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        fun saveToken(token: String) {
            preferences.edit().putString("jwt_token", token).apply()
        }

        fun getToken(): String? {
            return preferences.getString("jwt_token", null)
        }

        fun clearToken() {
            preferences.edit().remove("jwt_token").apply()
        }
    }


}

        data class LoginData(val email: String, val password: String)
        data class LoginResponse(val token: String)


//            val call = apiService.loginRequest(adminDataJson)
//            call.enqueue(object : Callback<LoginResponse> {
//                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
//                    if (response.isSuccessful) {
//                        // Parse the JSON response using Gson directly
//                        val loginResponse = response.body()
//
//                        val responseBody = response.body().toString()
//                        Log.d("Response Body", responseBody)
//
//                        if (loginResponse != null) {
//                            val token = loginResponse.token
//                            val userId = token?.let { it1 -> extractUserIdFromToken(it1) }
//
//                            Log.d("-----------", "onCreate: token  " + token)
//                            Log.d("-----------", "onCreate: userid  " + userId)
//
//
//                            // Extract the user ID from the token using jjwt
//
//
//                            // Check if token and userId are not null before using them
//                            if (token != null && userId != null) {
//                                // Save the token and userId securely (e.g., using SharedPreferences)
//                                val editor = sharedPreferences.edit()
//                                editor.putString("accessToken", token)
//                                editor.putString("User", userId)
//                                editor.apply()
//
//                                Toast.makeText(
//                                    this@LoginActivity,
//                                    "Login Successful",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                val intent =
//                                    Intent(applicationContext, MainActivity::class.java)
//                                startActivity(intent)
//                            } else {
//                                // Handle null values for token or userId
//                                Toast.makeText(
//                                    this@LoginActivity,
//                                    "Token or User ID is null",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        } else {
//                            // Handle a missing or invalid response
//                            Toast.makeText(
//                                this@LoginActivity,
//                                "Invalid server response",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    } else {
//                        // Handle specific error codes or messages from the server
//                        val errorBody = response.errorBody()?.string()
//                        if (errorBody != null) {
//                            // Handle the error response
//                            // ...
//                        } else {
//                            Toast.makeText(
//                                this@LoginActivity,
//                                "Login failed",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//                }
//
//                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
//                    // Handle network errors
//                    Toast.makeText(this@LoginActivity, "Network error", Toast.LENGTH_SHORT).show()
//                }
//            })
//
//        }
//
//    }
//
//    private fun extractUserIdFromToken(token: String): String? {
//        try {
//            val decodedToken = parse(token)
//            return decodedToken.User // Replace "User" with the actual field name in your JWT payload
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return null
//    }
//
//
//}
//
//object JwtParser {
//    private val gsonRef = Gson()
//
//    fun parse(token: String): DecodedLoginToken {
//        try {
//            val delimittedData = String(Base64.decode(token.split(".")[1], Base64.DEFAULT), Charsets.UTF_8)
//            val result = gsonRef.fromJson(delimittedData, DecodedLoginToken::class.java)
//            return result
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return DecodedLoginToken(null) // Return an empty DecodedLoginToken in case of errors
//    }
//}
//data class DecodedLoginToken(val User: String?)
//
//data class LoginData(
//    val email : String,
//    val password :String
//)
//data class LoginResponse(
//    @SerializedName("token")
//    val token: String?,
//    @SerializedName("User")
//    val User: String?
//) {
//    fun string(): Any {
//return true
//    }
//}






