//package com.example.weatherapp
//
//import android.support.v7.widget.RecyclerView
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import com.example.weatherapp.Data.WeatherResponse
//import kotlinx.android.synthetic.main.list_weather.view.*
//
//class WeatherAdapter(private var weatherResponse: WeatherResponse): RecyclerView.Adapter<WeatherAdapter.ViewHolder>(){
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.ctx).inflate(R.layout.list_weather, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun getItemCount(): Int = weatherResponse.size
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bind(weatherResponse[position])
//    }
//
//    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private lateinit var weatherResponse: WeatherResponse
//
//        fun bind(weatherResponse: WeatherResponse) {
//            this.weatherResponse = weatherResponse
//            itemView.city.text = weatherResponse.name
//            itemView.country.text = weatherResponse.sys.country
//            itemView.degree.text = weatherResponse.main.temp.toString()
//        }
//    }
//}