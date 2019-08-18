package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import com.google.android.material.snackbar.Snackbar
import androidx.core.app.ActivityCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.weatherapp.Data.NetworkService
import com.example.weatherapp.Data.WeatherResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.weather_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class CurrentActivity : AppCompatActivity() {

    private val service = NetworkService()
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    protected var mLastLocation: Location? = null



    companion object {

        private val TAG = "LocationProvider"

        private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_details)

        mSwipeRefreshLayout = findViewById(R.id.pullToRefresh)

        mSwipeRefreshLayout.setOnRefreshListener { getLastLocation() }
        mSwipeRefreshLayout.setColorSchemeResources(
            R.color.gray,
            R.color.black,
            R.color.gray
        )

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getLastLocation()
    }

    override fun onStart() {
        super.onStart()

        if (!checkPermissions()) {
            requestPermissions()
        } else {
            getLastLocation()
        }
    }

    private fun showMessage(text: String) {
        val container = findViewById<View>(R.id.main_activity_container)
        if (container != null) {
            Toast.makeText(this@CurrentActivity, text, Toast.LENGTH_LONG).show()
        }
    }

    private fun showSnackbar(
        mainTextStringId: Int, actionStringId: Int,
        listener: View.OnClickListener
    ) {

        Toast.makeText(this@CurrentActivity, getString(mainTextStringId), Toast.LENGTH_LONG).show()
    }

    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            this@CurrentActivity,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                View.OnClickListener {
                    // Request permission
                    startLocationPermissionRequest()
                })

        } else {
            Log.i(TAG, "Requesting permission")
            startLocationPermissionRequest()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.size <= 0) {
                Log.i(TAG, "User interaction was cancelled.")
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation()
            } else {
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                    View.OnClickListener {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts(
                            "package",
                            BuildConfig.APPLICATION_ID, null
                        )
                        intent.data = uri
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    })
            }
        }
    }

    private val callback = object : Callback<WeatherResponse> {
        override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
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
        personName.text = weatherResponse.name
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

    @SuppressLint("MissingPermission")
    private fun checkConnectivity(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
        if (!isConnected) {
            mSwipeRefreshLayout.isRefreshing = false
            Snackbar.make(weather_detail, "Check network connection", Snackbar.LENGTH_LONG).show()
        } else {

            mSwipeRefreshLayout.isRefreshing = false

            Toast.makeText(this@CurrentActivity, "Connected", Toast.LENGTH_SHORT).show()
        }
        return isConnected
    }

    fun getWeatherData(data: HashMap<String, String>) {
        service.getWeather(data, callback)
    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        mFusedLocationClient!!.lastLocation
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result != null) {
                    mLastLocation = task.result

                    val lati = mLastLocation!!.latitude
                    val longi = mLastLocation!!.longitude


                    val data = HashMap<String, String>()
                    data["lat"] = "$lati"
                    data["lon"] = "$longi"
                    data["appid"] = "fa76a0e2ec6d4703bbf92ba5ac855efe"

                    if (checkConnectivity()) {
                        getWeatherData(data)
                    }

                    Log.d(TAG, "Latitude:$lati ")
                    Log.d(TAG, "Longitude:$longi ")
                } else {
                    Log.w(TAG, "getLastLocation:exception", task.exception)
                    showMessage(getString(R.string.no_location_detected))
                }
            }
    }

}