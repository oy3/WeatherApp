package com.example.weatherapp.Room

import android.arch.persistence.room.*

@Dao
interface WeatherDoa {

    @Query("SELECT * FROM Location ORDER BY Id DESC")
    fun getAllCities(): List<Location>


    @Insert
    fun insertAllCities(location: Location)

    @Query("DELETE FROM Location WHERE Id = :Id")
    fun deleteCitybyId(Id: Int)


}