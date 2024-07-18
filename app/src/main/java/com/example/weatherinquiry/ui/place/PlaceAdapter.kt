package com.example.weatherinquiry.ui.place

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.edit
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LifecycleEventObserver
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherinquiry.SunnyWeatherApplication
import com.example.weatherinquiry.databinding.PlaceItemBinding
import com.example.weatherinquiry.logic.model.Place
import com.example.weatherinquiry.ui.weather.WeatherActivity
import com.google.gson.Gson

class PlaceAdapter(private val fragment: PlaceFragment, private val placeList: List<Place>) :
    RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    inner class ViewHolder(binding: PlaceItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val placeName: TextView = binding.placeName
        val placeAddress: TextView = binding.placeAddress
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PlaceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return placeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.address

        holder.itemView.setOnClickListener {
            val place=placeList[position]
            val activity=fragment.activity
            if (activity is WeatherActivity){
                activity.binding.drawerLayout.closeDrawers()

                activity.viewModel.locationLng=place.location.lng
                activity.viewModel.locationLat=place.location.lat
                activity.viewModel.placeName=place.name
                activity.refreshWeather()
            }
            else{
                var intent=Intent(SunnyWeatherApplication.context,WeatherActivity::class.java).apply {
                    putExtra("location_lng",place.location.lng)
                    putExtra("location_lat",place.location.lat)
                    putExtra("place_name",place.name)
                }
                fragment.startActivity(intent)
                activity?.finish()
            }

            fragment.viewModel.savePlace(place)

        }

    }
}