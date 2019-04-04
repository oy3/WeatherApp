package com.example.weatherapp

import com.example.weatherapp.Const.Constants.API_KEY
import com.example.weatherapp.Const.Constants.BASE_URL
import com.example.weatherapp.Const.Constants.city
import com.example.weatherapp.Data.WeatherResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class NetworkService {
    private var service: WeatherApi
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(provideOkHttpClient())
            .build()

        service = retrofit.create(WeatherApi::class.java)
    }

    private fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        return logging
    }

    private fun provideOkHttpClient(): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(provideLoggingInterceptor())
        return httpClient.build()
    }

    fun getWeather(queries: HashMap<String, String>, callback: Callback<WeatherResponse>) {
        service.getLocationDetails(queries).enqueue(callback)
    }

}