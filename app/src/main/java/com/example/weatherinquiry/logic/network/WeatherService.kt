package com.example.weatherinquiry.logic.network

import com.example.weatherinquiry.SunnyWeatherApplication
import com.example.weatherinquiry.logic.model.DailyResponse
import com.example.weatherinquiry.logic.model.RealTimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherService {

    @GET("v2.5/${SunnyWeatherApplication.token}/{lng},{lat}/realtime.json")
    fun getRealTimeWeather(
        @Path("lng") lng: String,
        @Path("lat") lat: String
    ): Call<RealTimeResponse>

    @GET("v2.5/${SunnyWeatherApplication.token}/{lng},{lat}/daily.json")
    fun getDailyWeather(@Path("lng") lng: String, @Path("lat") lat: String): Call<DailyResponse>
}