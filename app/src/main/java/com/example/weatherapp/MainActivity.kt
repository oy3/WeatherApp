package com.example.weatherapp

import android.arch.persistence.room.Room
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
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

    lateinit var recyclerView: RecyclerView
    private val TAG = "Main Activity"
    lateinit var db: WeatherDatabase

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



        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = WeatherAdapter(cityList, this, this)

        cityBtn.setOnClickListener { view ->
            startActivity(Intent(this@MainActivity, CreateCity::class.java))

        }

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
        builder.setTitle("Delete Task")
        builder.setMessage("Do you want to delete task?")
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