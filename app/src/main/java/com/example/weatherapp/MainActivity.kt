package com.example.weatherapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.weatherapp.Room.Location
import com.example.weatherapp.Room.WeatherDatabase
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), WeatherAdapter.WeatherSelectionRecyclerViewClickListener {

    companion object {
        val INTENT_WEATHER_KEY = "city"
    }

    override fun cityItemClicked(location: Location) {
        showCityDetails(location)
    }

    override fun cityItemLongClicked(location: Location) {
        showDeleteDialog(location.Id)
    }

    lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private val TAG = "Main Activity"
    lateinit var db: WeatherDatabase
    var isSpinnerTouched = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        db = Room.databaseBuilder(
            this.applicationContext,
            WeatherDatabase::class.java,
            "Weather_database"
        ).allowMainThreadQueries()
            .build()

        val cityList: List<Location> = db.weatherDoa().getAllCities()
        val cityNames = cityList.map {
            it.city + ", " + it.country
        }

         val spinner: Spinner = findViewById(R.id.spinnerV)
        val arrayAdapter = ArrayAdapter(this, android.R.layout.select_dialog_item, cityNames)
        spinner.adapter = arrayAdapter


        spinner.setOnTouchListener { v, event ->
            isSpinnerTouched = true
            false
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if(isSpinnerTouched){
//                    Toast.makeText(this@MainActivity, "Selected: " + cityNames[position], Toast.LENGTH_SHORT).show()
                    showCityDetails(cityList[position])
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code to perform some action when nothing is selected
            }
        }
//
//        recyclerView = findViewById(R.id.recyclerView)
//        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
//        recyclerView.adapter = WeatherAdapter(cityList, this, this)

        cityBtn.setOnClickListener { view ->
            startActivity(Intent(this@MainActivity, CreateCity::class.java))

        }

//        locationBtn.setOnClickListener{ view ->
//            startActivity(Intent(this@MainActivity, CurrentActivity::class.java))

        locationBtn.setOnClickListener{ view ->
                startActivity(Intent(this@MainActivity, CurrentActivity::class.java))
        }
    }

    private fun showCityDetails(location: Location) {
        val cityDetailIntent = Intent(this, WeatherDetails::class.java)
        cityDetailIntent.putExtra(INTENT_WEATHER_KEY, location)
        startActivity(cityDetailIntent)
    }

    private fun showDeleteDialog(id: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete City")
        builder.setMessage("Do you want to delete city?")
        builder.setPositiveButton("YES") { dialog, which ->


            db.weatherDoa().deleteCitybyId(id)
//            adapter.updateTask(taskList!!)
            Toast.makeText(this, "Deleted city successfully!!", Toast.LENGTH_SHORT).show()
            refreshList()
        }
        builder.setNegativeButton("No") { dialog, which ->
            refreshList()
        }

        val dialog: AlertDialog = builder.create()

        dialog.show()

    }

    private fun refreshList() {
        val cityList: List<Location> = db.weatherDoa().getAllCities()
        recyclerView.adapter = WeatherAdapter(cityList, this, this)
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