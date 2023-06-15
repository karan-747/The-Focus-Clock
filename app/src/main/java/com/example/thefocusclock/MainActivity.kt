package com.example.thefocusclock

import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity() {
    private lateinit var btnProceed: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(! NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()){
            Toast.makeText(this,"Please Enable Notification Permission For Better Experience",Toast.LENGTH_SHORT).show()
        }
        val window: Window = window
        WindowInsetsControllerCompat(window,window.decorView).isAppearanceLightStatusBars = true



        //supportActionBar?.setBackgroundDrawable(ColorDrawable(getColor(R.color.my_grey)))
        supportActionBar?.hide()
       // supportActionBar?.title = Html.fromHtml("<font color='#CF1321'>The FocusClock</font>")

        window.statusBarColor=getColor(R.color.my_grey)
        btnProceed=findViewById(R.id.btnProceed)


        btnProceed.setOnClickListener {
            goToHomeActivity()
        }

    }
    fun goToHomeActivity(){
        startActivity(Intent(this@MainActivity,HomeActivity::class.java))
    }

    fun Window.setLightStatusBars(b: Boolean) {
        WindowCompat.getInsetsController(this, decorView)?.isAppearanceLightStatusBars = b
    }
}
