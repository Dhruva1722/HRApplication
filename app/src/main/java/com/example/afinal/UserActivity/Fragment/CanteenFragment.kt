package com.example.afinal.UserActivity.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.afinal.R
import com.example.afinal.UserActivity.BuyCoupen
import com.example.afinal.UserActivity.SaturdaySpecial
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CanteenFragment : Fragment() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: MenuPagerAdapter

    private lateinit var buyCoupenCard : LinearLayout
    private lateinit var saturdaySpecial : LinearLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_canteen, container, false)
        viewPager = view.findViewById(R.id.menuViewPager)
        tabLayout = view.findViewById(R.id.tabLayout)

        adapter = MenuPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Today's Menu"
                1 -> tab.text = "Tomorrow Menu"
            }
        }.attach()

        buyCoupenCard = view.findViewById(R.id.buyCoupenCard)
        buyCoupenCard.setOnClickListener {
            val intent = Intent(activity, BuyCoupen::class.java)
            startActivity(intent)
        }
        saturdaySpecial = view.findViewById(R.id.saturdayCard)
        saturdaySpecial.setOnClickListener {
            val intent = Intent(activity, SaturdaySpecial::class.java)
            startActivity(intent)
        }
        return view
    }
}
class MenuPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TodayFoodFragment()
            1 -> TommorrowFoodFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}



