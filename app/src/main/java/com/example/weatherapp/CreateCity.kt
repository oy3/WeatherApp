package com.example.weatherapp

import android.content.Context
import androidx.room.Room
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.example.weatherapp.Room.Location
import com.example.weatherapp.Room.WeatherDatabase
import kotlinx.android.synthetic.main.create_weather.*

class CreateCity() : AppCompatActivity() {

    private var TAG: String = "CreateCity"
    lateinit var context: Context

    private fun vibrate(){
        val vibratorService = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibratorService.vibrate(500)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_weather)


        val db = Room.databaseBuilder(
            this.applicationContext,
            WeatherDatabase::class.java,
            "Weather_database"
        ).allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

        addBtn.setOnClickListener { view ->
            if (cityTxt.text.isNullOrEmpty() && countryTxt.text.isNullOrEmpty()) {
                error_mssg.text = "Please input city and country"
                city_layout.setHintTextAppearance(R.style.error_appearance)
                country_layout.setHintTextAppearance(R.style.error_appearance)
            } else if (cityTxt.text.isNullOrBlank()) {
                error_mssg.text = "Please input city"
                city_layout.setHintTextAppearance(R.style.error_appearance)
                country_layout.setHintTextAppearance(R.style.success_appearance)
            } else if (countryTxt.text.isNullOrEmpty()) {
                error_mssg.text = "Please input country"
                country_layout.setHintTextAppearance(R.style.error_appearance)
                city_layout.setHintTextAppearance(R.style.success_appearance)
            } else {
                Log.d(
                    TAG, "onClick: City:  ${cityTxt.text.toString()} " +
                            ", Country: ${countryTxt.text.toString()} "
                )
                db.weatherDoa().insertAllCities(Location(cityTxt.text.toString(), countryTxt.text.toString()))
                startActivity(Intent(this@CreateCity, MainActivity::class.java))
            }
        }
    }
}

