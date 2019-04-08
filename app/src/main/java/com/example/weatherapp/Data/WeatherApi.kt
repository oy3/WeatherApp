package com.example.weatherapp.Data

import com.example.weatherapp.Data.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap


interface WeatherApi {
    @GET("data/2.5/weather")
    fun getLocationDetails(@QueryMap queries: HashMap<String, String>): Call<WeatherResponse>
}

//val url ="http://api.openweathermap.org/data/2.5/weather?q=Lagos,NG&units=metric&appid=fa76a0e2ec6d4703bbf92ba5ac855efe"
//(@Query("city") city:String, @Query("key") key:String)
