package com.example.weatherapp.Data

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("coord") val coordinate: Coordinate,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Int,
    val sys: Sys,
    val id: Int,
    val name: String,
    @SerializedName("cod") val code: Int
)

data class Coordinate(
    val lon: Double,
    val lat: Double
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)


data class Main(
    val temp: Double,
    val pressure: Double,
    val humidity: Double,
    val temp_min: Double,
    val temp_max: Double
)


data class Wind(
    val speed: Double,
    val deg: Int
)

data class Clouds(
    val all: Int
)


data class Sys(
    val type: Int,
    val id: Int,
    val message: Double,
    val country: String,
    val sunrise: Int,
    val sunset: Int
)


