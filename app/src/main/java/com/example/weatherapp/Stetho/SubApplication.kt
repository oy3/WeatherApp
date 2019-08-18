package com.example.weatherapp.Stetho

import android.app.Application
import com.facebook.stetho.Stetho

class SubApplication: Application() {


    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)
    }

}