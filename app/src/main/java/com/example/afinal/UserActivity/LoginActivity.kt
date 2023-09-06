package com.example.afinal.UserActivity


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.afinal.MainActivity
import com.example.afinal.MapActivity.MapsActivity
import com.example.afinal.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser



class LoginActivity : AppCompatActivity() {


    private lateinit var newUserTextView: TextView
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton:Button
    private lateinit var passResetBtn:TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.afinal.R.layout.activity_login)


        mAuth = FirebaseAuth.getInstance()


        emailEditText = findViewById(R.id.loginEmailID)
        passwordEditText = findViewById(R.id.loginPasswordID)
        loginButton = findViewById(R.id.loginBtnID)
        newUserTextView = findViewById(R.id.newUserID)
        passResetBtn = findViewById(R.id.forgetPAsswordID)

        mAuth = FirebaseAuth.getInstance()


        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE) // Initialize it here


        val currentUser = mAuth.currentUser
        if (currentUser != null || sharedPreferences.getBoolean("isLoggedIn", false)) {
            navigateToMapsActivity()
            return
        }

        loginButton.setOnClickListener {
            loginUser()
        }

        newUserTextView.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        passResetBtn.setOnClickListener {
            val email = emailEditText.text.toString()

            if (email.isNotEmpty()) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Password reset email sent successfully
                            Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                        } else {
                            // Password reset email failed to send
                            Toast.makeText(this, "Failed to send password reset email", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter your registered email", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun loginUser() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        // Check if the email is valid
        if (!isValidEmail(email)) {
            emailEditText.error = "Invalid email address"
            return
        }

        // Check if the password is valid (e.g., meets length requirements)
        if (!isValidPassword(password)) {
            passwordEditText.error = "Invalid password"
            return
        }

        val currentUser = mAuth.currentUser
        if (currentUser != null || sharedPreferences.getBoolean("isLoggedIn", false)) {

            mAuth.currentUser?.reload()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val isAccountDeleted = mAuth.currentUser == null
                    if (isAccountDeleted) {
                        // User's account has been deleted, clear preferences and show a message
                        sharedPreferences.edit().clear().apply()
                        Toast.makeText(
                            this,
                            "Your account has been deleted. Please sign up again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Account still exists, navigate to MapsActivity
                        Toast.makeText(
                            this,
                            "Pls Register first.",
                            Toast.LENGTH_SHORT
                        ).show()
                        navigateToMapsActivity()
                    }
                }
            }
        }

//        mAuth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // User login successful
//                    val user: FirebaseUser? = mAuth.currentUser
//                    if (user != null) {
//                        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
//                        navigateToMapsActivity()
//                    }
//                } else {
//                    // User login failed
//                    Toast.makeText(this, "Login failed.", Toast.LENGTH_SHORT).show()
//                }
//            }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    private fun navigateToMapsActivity() {
        mAuth.currentUser?.reload()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val isAccountDeleted = mAuth.currentUser == null
                if (isAccountDeleted) {
                    // User's account has been deleted, show a message
                    Toast.makeText(
                        this,
                        "Your account has been deleted. Please sign up again.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Account still exists, navigate to MapsActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_left, R.anim.slide_left_out)
                    finish()
                }
            }
        }
    }
//    private fun navigateToMapsActivity() {
//        val intent = Intent(this,MainActivity::class.java)
//        startActivity(intent)
//        overridePendingTransition(R.anim.slide_left, R.anim.slide_left_out);
//        finish() // Optionally, finish the current activity if needed
//    }
}