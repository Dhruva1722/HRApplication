package com.example.afinal.UserActivity.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.afinal.MapActivity.MapsActivity
import com.example.afinal.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

        private lateinit var continuebtn : Button
        private lateinit var yourLocation : TextInputEditText
        private lateinit var destinationLocation : TextInputEditText



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val db = FirebaseFirestore.getInstance()
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        continuebtn = view.findViewById(R.id.continueBtn)
        yourLocation = view.findViewById(R.id.yourLocationID)
        destinationLocation = view.findViewById(R.id.destinationID)

        continuebtn.setOnClickListener {

            val yourLocation = yourLocation.text.toString()
            val destinationLocation = destinationLocation.text.toString()

            val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (userId != null) {
                val userDocRef = db.collection("users").document(userId)

                val locationsCollection = userDocRef.collection("location_points")

                val locationData = hashMapOf(
                    "yourLocation" to yourLocation,
                    "destinationLocation" to destinationLocation
                )

                locationsCollection.add(locationData)
                    .addOnSuccessListener { documentReference ->
                        val intent = Intent(context, MapsActivity::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Error adding document: $e", Toast.LENGTH_SHORT).show()
                    }
            }

        }

        return view
    }




}