package com.example.weatherinquiry.logic

import androidx.lifecycle.liveData
import com.example.weatherinquiry.logic.dao.PlaceDao
import com.example.weatherinquiry.logic.model.Place
import com.example.weatherinquiry.logic.model.Weather
import com.example.weatherinquiry.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

object Repository {
    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }

    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is${placeResponse.status}"))
        }
    }

    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
        coroutineScope {
            val deferredRealtime = async {
                SunnyWeatherNetwork.getRealTimeWeather(lng, lat)
            }
            val deferredDaily = async {
                SunnyWeatherNetwork.getDailyWeather(lng, lat)
            }
            val realTimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            if (realTimeResponse.status == "ok" && dailyResponse.status == "ok") {
                var weather = Weather(realTimeResponse.result.realtime, dailyResponse.result.daily)
                Result.success(weather)
            } else {
                Result.failure(
                    RuntimeException(
                        "realtime response status is ${realTimeResponse.status}" +
                                "daily response status is ${dailyResponse.status}"
                    )
                )
            }
        }
    }

    fun savePlace(place: Place) =
        PlaceDao.savePlace(place)


    fun getSavedPlace() =
        PlaceDao.getSavedPlace()

    fun isPlaceSaved() =
        PlaceDao.isPlaceSaved()


}
