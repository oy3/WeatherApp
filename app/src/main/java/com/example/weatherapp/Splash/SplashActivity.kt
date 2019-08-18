package com.example.weatherapp.Splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import java.lang.Exception

class SplashActivity  : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val background = object : Thread() {
            override fun run() {
                try {
                    sleep(3000)

                    val intent = Intent(baseContext, MainActivity::class.java)
                    startActivity(intent)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        background.start()
    }
}