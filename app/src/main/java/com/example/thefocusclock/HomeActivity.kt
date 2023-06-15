package com.example.thefocusclock

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.widget.Toast
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    private lateinit var homeFragment: HomeFragment;
    private lateinit var aboutFragment: AboutFragment
    private lateinit var statisticsFragment: StatisticsFragment
    private lateinit var bottomNavigtionView:BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportActionBar?.hide()
        window.statusBarColor=getColor(R.color.my_grey)

        val window: Window = window
        WindowInsetsControllerCompat(window,window.decorView).isAppearanceLightStatusBars = true

        homeFragment= HomeFragment()
        aboutFragment= AboutFragment()
        statisticsFragment= StatisticsFragment()
        bottomNavigtionView=findViewById(R.id.bottomNavigationView)
        setCurrentFragment(homeFragment)

        bottomNavigtionView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.navBtnHome->{
                    setCurrentFragment(homeFragment)
                }
                R.id.navBtnStatistics->{
                    setCurrentFragment(statisticsFragment)
                }
                R.id.navBtnAbout->{
                    setCurrentFragment(aboutFragment)
                }
            }
            true
        }

    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity()
            finish()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }


    private fun setCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragments,fragment)

            commit()
        }
    }
}