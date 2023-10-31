package com.example.afinal.UserActivity.Adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.afinal.UserActivity.Fragment.TodayFoodFragment
import com.example.afinal.UserActivity.Fragment.TommorrowFoodFragment

class MenuPagerAdapter (fragment: Fragment, private val menuRef: String) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TodayFoodFragment().apply {
                arguments = Bundle().apply {
                    putString("menuRef", menuRef)
                }
            }
            1 -> TommorrowFoodFragment().apply {
                arguments = Bundle().apply {
                    putString("menuRef", menuRef)
                }
            }
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

}