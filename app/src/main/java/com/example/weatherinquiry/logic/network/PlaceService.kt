package com.example.weatherinquiry.logic.network

import com.example.weatherinquiry.SunnyWeatherApplication
import com.example.weatherinquiry.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceService {
    @GET("v2/place?token=${SunnyWeatherApplication.token}&lang=zn_CN")
    fun searchPlaces(@Query("query") query: String): Call<PlaceResponse>
}