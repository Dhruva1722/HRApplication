package com.example.afinal.UserActivity.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.afinal.MapActivity.MapsActivity
import com.example.afinal.R

class HomeFragment : Fragment() {

        private lateinit var continuebtn : Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        continuebtn = view.findViewById(R.id.continueBtn)

        continuebtn.setOnClickListener {

            val intent = Intent(activity, MapsActivity::class.java)
            startActivity(intent)
        }


        return view
    }

}