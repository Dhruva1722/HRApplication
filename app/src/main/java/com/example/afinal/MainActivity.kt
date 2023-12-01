package com.example.afinal

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.afinal.UserActivity.ComplaintActivity
import com.example.afinal.UserActivity.Fragment.AttendanceFragment
import com.example.afinal.UserActivity.Fragment.CanteenFragment
import com.example.afinal.UserActivity.Fragment.EventFragment
import com.example.afinal.UserActivity.Fragment.HomeFragment
import com.example.afinal.UserActivity.Fragment.ProfileFragment
import com.example.afinal.UserActivity.HelpActivity
import com.example.afinal.UserActivity.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {


    private lateinit var helpBtn: ImageView
    private lateinit var logoutBtn: TextView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        helpBtn = findViewById(R.id.helpBtn)

        helpBtn.setOnClickListener { v ->
            showPopupMenu(v)
        }
        logoutBtn = findViewById(R.id.logout)
        logoutBtn.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.remove("isLoggedIn")
            editor.remove("User")
            editor.remove("userEmail")
            editor.remove("ATTENDANCE_STATUS_KEY")
            editor.remove("LAST_ATTENDANCE_DATE_KEY")
            editor.remove("DATE_KEY, DateTime")
            editor.apply()
            println("HERE --------------")
//            finishAffinity()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)


        actionBarDrawerToggle.syncState()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        toolbar = findViewById(R.id.toolbar)
        actionBarDrawerToggle.isDrawerIndicatorEnabled = true
        toolbar.navigationIcon!!
            .setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)


        toolbar.setOnClickListener {
            if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener { item -> // Handle navigation item selection here
            //drawerLayout.closeDrawer(GravityCompat.START)
            var fragment: Fragment? = null
            if (item.itemId == R.id.nav_attendance) {
                fragment = AttendanceFragment()
                drawerLayout.closeDrawer(GravityCompat.START)
            } else if (item.itemId == R.id.nav_canteen) {
                fragment = CanteenFragment()
                drawerLayout.closeDrawer(GravityCompat.START)
            } else if (item.itemId == R.id.nav_help) {
                fragment = HomeFragment()
                drawerLayout.closeDrawer(GravityCompat.START)
            } else if (item.itemId == R.id.nav_event) {
                fragment = EventFragment()
                drawerLayout.closeDrawer(GravityCompat.START)
            }else if (item.itemId == R.id.nav_profile) {
                fragment = ProfileFragment()
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            fragment?.let { loadFragment(it) }
            true
        }
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        loadFragment(AttendanceFragment())
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.help_menu)

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_help -> {
                    val intent = Intent(this, HelpActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.action_complain -> {
                    val intent = Intent(this, ComplaintActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment: Fragment? = null
        if (item.itemId == R.id.bottomnav_home) {
            fragment = HomeFragment()
        } else if (item.itemId == R.id.bottomnav_attendence) {
            fragment = AttendanceFragment()
        } else if (item.itemId == R.id.bottomnav_account) {
            fragment = CanteenFragment()
        } else if (item.itemId == R.id.bottomnav_event) {
            fragment = EventFragment()
        }else if (item.itemId == R.id.bottomnav_profile) {
            fragment = ProfileFragment()
        }
        fragment?.let { loadFragment(it) }
        return true
    }

    fun loadFragment(fragment: Fragment?) {
        supportFragmentManager.beginTransaction().replace(R.id.relativelayout, fragment!!).commit()
    }
}




