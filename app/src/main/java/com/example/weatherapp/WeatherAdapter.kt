package com.example.weatherapp

import android.content.Context
import android.os.Vibrator
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.weatherapp.Room.Location

class WeatherAdapter(
    var location: List<Location>,
    var context: Context,
    private val clickListener: WeatherSelectionRecyclerViewClickListener
) : RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_weather, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return location.size
    }

    override fun onBindViewHolder(holder: WeatherAdapter.ViewHolder, position: Int) {
        holder.cityList.text = location.get(position).city
        holder.countryList.text = location.get(position).country

        holder.itemView.setOnClickListener {
            clickListener.cityItemClicked(location.get(position))

        }

        holder.itemView.setOnLongClickListener {
            val vibratorService = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibratorService.vibrate(500)

            clickListener.cityItemLongClicked(location[position])
            true
        }
    }

    interface WeatherSelectionRecyclerViewClickListener {
        fun cityItemLongClicked(location: Location)
        fun cityItemClicked(location: Location)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val cityList = itemView.findViewById(R.id.city) as TextView
        val countryList = itemView.findViewById(R.id.country) as TextView
    }
}