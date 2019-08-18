package com.example.weatherapp

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.example.weatherapp.Data.NetworkService
import com.example.weatherapp.Data.WeatherResponse
import com.example.weatherapp.Room.Location
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.GrayscaleTransformation
import kotlinx.android.synthetic.main.weather_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class WeatherDetails : AppCompatActivity() {

    private val TAG = "Weather Detail Activity"
    private val service = NetworkService()
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout


    companion object {
        val INTENT_WEATHER_KEY = "city"

        fun newIntent(context: Context, task: Location): Intent {
            val intent = Intent(context, WeatherDetails::class.java)
            intent.putExtra(INTENT_WEATHER_KEY, task)
            return intent
        }
    }

    private val cityIntent by lazy { intent.getParcelableExtra<Location>(INTENT_WEATHER_KEY) }


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
                updateWeather(weatherList!!)

            }
            mSwipeRefreshLayout.isRefreshing = false
        }

        override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
            mSwipeRefreshLayout.isRefreshing = false
            Snackbar.make(weather_detail, "Problem calling Weather API", Snackbar.LENGTH_LONG).show()
            Log.e(TAG, "Problem calling Weather API", t)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_details)

        mSwipeRefreshLayout = findViewById(R.id.pullToRefresh)

        mSwipeRefreshLayout.setOnRefreshListener { checkConnectivity() }
        mSwipeRefreshLayout.setColorSchemeResources(
            R.color.gray,
            R.color.black,
            R.color.gray
        )

        checkConnectivity()
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

    private fun getCurrentTime(): String {
        val time = GregorianCalendar()
        val hour = time.get(Calendar.HOUR_OF_DAY)

        return when {
            hour < 12 -> "Good Morning"
            hour in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    private fun updateWeather(weatherResponse: WeatherResponse) {
        greeting.text = getCurrentTime() + " from "
        day.text = getDay(weatherResponse.dt.toLong())
        degreeNo.text = weatherResponse.main.temp.toString() + "°"
        sunrise.text = getTime(weatherResponse.sys.sunrise.toLong())
        wind.text = weatherResponse.wind.speed.toString() + " n/s SW"
        sunset.text = getTime(weatherResponse.sys.sunset.toLong())
        val city = weatherResponse.name
        val country = weatherResponse.sys.country
        location.text = "$city, $country"
        if (weatherResponse.weather.isNotEmpty()) {
            val iconPic = Picasso.with(this)
                .load("http://openweathermap.org/img/w/${weatherResponse.weather[0].icon}.png")
//                .transform(GrayscaleTransformation())
                .into(weatherIcon)
            Log.v(TAG, "Weather icon:$iconPic ")

        }
        date.text = getDate(weatherResponse.dt.toLong())

    }

    private fun checkConnectivity() {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
        if (!isConnected) {
            mSwipeRefreshLayout.isRefreshing = false
            Snackbar.make(weather_detail, "Check network connection", Snackbar.LENGTH_LONG).show()
        } else {
            mSwipeRefreshLayout.isRefreshing = false
            Toast.makeText(this@WeatherDetails, "Connected", Toast.LENGTH_SHORT).show()
            val data = HashMap<String, String>()
            personName.text = cityIntent.city

            val wCity = cityIntent.city
            val wCountry =  cityIntent.country
            data["q"] = "$wCity,$wCountry"
            data["units"] = "metric"
            data["appid"] = "fa76a0e2ec6d4703bbf92ba5ac855efe"
            service.getWeather(data, callback)
        }
    }


}