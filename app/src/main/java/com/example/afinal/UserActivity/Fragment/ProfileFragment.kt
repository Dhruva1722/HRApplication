package com.example.afinal.UserActivity.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.afinal.MainActivity
import com.example.afinal.R
import com.example.afinal.UserActivity.LeaveStatus
import com.example.afinal.UserActivity.Profile_Page
import com.example.afinal.UserActivity.TripDetails
import com.google.gson.annotations.SerializedName


class ProfileFragment : Fragment() {

    private lateinit var profilePage: TextView
    private lateinit var tripPage: TextView
    private lateinit var leavePage: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val view = inflater.inflate(R.layout.fragment_profile, container, false)

        profilePage = view.findViewById(R.id.edit_Profile)
        profilePage.setOnClickListener{
            Log.d("--------------", "onCreateView: ----------- button clicked!!! profilePage")
            val intent = Intent(requireContext(), Profile_Page::class.java)
            startActivity(intent)
        }

        tripPage = view.findViewById(R.id.trip_Details)
        tripPage.setOnClickListener{
            Log.d("--------------", "onCreateView: ----------- button clicked!!!   tripPage")

            val intent = Intent(requireContext(), TripDetails::class.java)
            startActivity(intent)
        }

        leavePage = view.findViewById(R.id.leave_Status)
        leavePage.setOnClickListener{
            Log.d("--------------", "onCreateView: ----------- button clicked!!!   leavePage")

            val intent = Intent(requireContext(), LeaveStatus::class.java)
            startActivity(intent)
        }
        return view
    }

}
