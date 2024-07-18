package com.example.weatherinquiry.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.example.weatherinquiry.SunnyWeatherApplication
import com.example.weatherinquiry.logic.model.Place
import com.google.gson.Gson

object PlaceDao {
    private fun sharedPreferences() =
        SunnyWeatherApplication.context.getSharedPreferences("sunny", Context.MODE_PRIVATE)

    fun savePlace(place: Place) =
        sharedPreferences().edit {
            putString("place", Gson().toJson(place))
        }


    fun getSavedPlace(): Place {
        val placeJson = sharedPreferences().getString("place", "")
        return Gson().fromJson(placeJson, Place::class.java)
    }

    fun isPlaceSaved() = sharedPreferences().contains("place")
}