package com.example.afinal.UserActivity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.afinal.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserDetails : AppCompatActivity() {

    private lateinit var helpBtn: ImageView

    private lateinit var logoutbtn: Button
    private lateinit var savebtn: Button

    private lateinit var radioGroup: RadioGroup
    private lateinit var busRadio: RadioButton
    private lateinit var bikeRadio: RadioButton
    private lateinit var trainRadio: RadioButton
    private lateinit var flightRadio: RadioButton
    private lateinit var uploadButton: ImageView
    private lateinit var imgContainer: RelativeLayout


    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)


        radioGroup = findViewById(R.id.idRadioGroup)
        uploadButton = findViewById(R.id.uploadButton)
        savebtn = findViewById(R.id.saveBtn)

        busRadio = findViewById(R.id.idBtnBusRadio)
        bikeRadio = findViewById(R.id.idBtnBikeRadio)
        trainRadio = findViewById(R.id.idBtnTrainRadio)
        flightRadio = findViewById(R.id.idBtnFlightRadio)
        imgContainer = findViewById(R.id.imageContainer)


        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("User", null)

        val radioGroup = findViewById<RadioGroup>(R.id.idRadioGroup)
        val selectedTransportationMode = when (radioGroup.checkedRadioButtonId) {
            R.id.idBtnBusRadio -> "Bus"
            R.id.idBtnBikeRadio -> "Bike"
            R.id.idBtnTrainRadio -> "Train"
            R.id.idBtnFlightRadio -> "Flight"
            else -> null
        }


        helpBtn = findViewById(R.id.helpBtn)

        helpBtn.setOnClickListener { v ->
            showPopupMenu(v)
        }

        logoutbtn = findViewById(R.id.logoutBtn)

        logoutbtn.setOnClickListener {

        }


    }

    private fun overridePendingTransition(slideLeft: Int) {

    }


    private fun showSuccessMessage() {
        val thankYouTextView = findViewById<TextView>(R.id.thankYouTextView)
        val successIconImageView = findViewById<ImageView>(R.id.successIconImageView)

        thankYouTextView.visibility = View.VISIBLE
        successIconImageView.visibility = View.VISIBLE
    }


    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.help_menu) // Inflate the menu resource

        // Set a listener for menu item clicks
        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_help -> {
                    // Handle Help action
                    val intent = Intent(this, HelpActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.action_complain -> {
                    // Handle Feedback action
                    val intent = Intent(this, ComplaintActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }
}
