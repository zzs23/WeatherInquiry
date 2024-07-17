package com.example.weatherinquiry.ui.place

import androidx.lifecycle.*
import com.example.weatherinquiry.logic.Repository
import com.example.weatherinquiry.logic.model.Place


class PlaceViewModel :ViewModel(){
    private val searchLiveData = MutableLiveData<String>()

    var placeList = ArrayList<Place>()

    val placeLiveData = searchLiveData.switchMap { query ->
        Repository.searchPlaces(query)
    }

    fun searchPlaces(query:String){
        searchLiveData.value=query
    }
}