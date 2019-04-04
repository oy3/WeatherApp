package com.example.weatherapp

import android.content.Context
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.example.weatherapp.Data.WeatherResponse
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.weather_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    //    lateinit var recyclerView: RecyclerView
    private val TAG = "Main Activity"
    private val service = NetworkService()


    private val callback = object : Callback<WeatherResponse> {
        override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
//            when (response.code()) {
//                200 -> {
//
//                }
//                401 -> {
//
//                }
//            }
            response.isSuccessful.let {
                val weatherList = response.body()
//                recyclerView.adapter = WeatherAdapter(weatherList!!)
                updateWeather(weatherList!!)

            }
        }

        override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
            Log.e(TAG, "Problem calling Weather API", t)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_details)


//        recyclerView = findViewById(R.id.recyclerView)
//        recyclerView.layoutManager = LinearLayoutManager(this)
        checkConnectivity()
        getTimeFromAndroid()
    }

    private fun getDate(date: Long): String {
        val timeFormatter = SimpleDateFormat("dd MMMM yyyy")
        return timeFormatter.format(Date(date * 1000L))
    }

    private fun getTime(time: Long): String {
        val timeFormatter = SimpleDateFormat("HH:MM a")
        return timeFormatter.format(Date(time * 1000L))
    }

    private fun getDay(day: Long): String {
        val timeFormatter = SimpleDateFormat("EEEE")
        return timeFormatter.format(Date(day * 1000L))
    }

    private fun getTimeFromAndroid(): String {
        val timeFormatter = SimpleDateFormat("HH:MM a")
        val time = timeFormatter.format(Date())

//        Toast.makeText(this, time, Toast.LENGTH_SHORT).show()

        if (time >= "00:00 AM" && time <= "11:59 PM") {
            return "Good Morning"
//            Toast.makeText(this, "Good Morning", Toast.LENGTH_SHORT).show()
        } else if (time >= "12:00 PM" && time <= "03:49 PM") {
            return "Good Afternoon"
//            Toast.makeText(this, "Good Afternoon", Toast.LENGTH_SHORT).show()
        } else if (time >= "04:00 PM" && time <= "09:00 PM") {
            return "Good Evening"
//            Toast.makeText(this, "Good Evening", Toast.LENGTH_SHORT).show()
        } else if (time >= "09:00 PM" && time <= "11:59 AM") {
            return "Good Night"
//            Toast.makeText(this, "Good Night", Toast.LENGTH_SHORT).show()
        }
        return null.toString()
    }

    private fun updateWeather(weatherResponse: WeatherResponse) {
        greeting.text = getTimeFromAndroid() + ", "
        day.text = getDay(weatherResponse.dt.toLong())
        degreeNo.text = weatherResponse.main.temp.toString() + "°"
        sunrise.text = getTime(weatherResponse.sys.sunrise.toLong())
        wind.text = weatherResponse.wind.speed.toString() + " n/s SW"
        sunset.text = getTime(weatherResponse.sys.sunset.toLong())
        val city = weatherResponse.name
        val country = weatherResponse.sys.country
        location.text = "$city, $country"
        Picasso.with(this)
            .load("http://openweathermap.org/img/w/${weatherResponse.weather}.png")
            .into(weatherIcon)
        date.text = getDate(weatherResponse.dt.toLong())

    }

    private fun checkConnectivity() {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
        if (!isConnected) {
            Toast.makeText(this@MainActivity, "Check network connection", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this@MainActivity, "Connected", Toast.LENGTH_SHORT).show()
            val data = HashMap<String, String>()
            data["q"] = "Lagos,NG"
            data["units"] = "metric"
            data["appid"] = "fa76a0e2ec6d4703bbf92ba5ac855efe"
            service.getWeather(data, callback)
        }
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Tap back again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }
}