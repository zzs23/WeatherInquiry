package com.example.weatherinquiry.ui.weather

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.hardware.input.InputManager
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weatherinquiry.R
import com.example.weatherinquiry.databinding.ActivityWeatherBinding
import com.example.weatherinquiry.logic.model.Weather
import com.example.weatherinquiry.logic.model.getSky
import com.example.weatherinquiry.ui.place.WeatherViewModel
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherActivity : AppCompatActivity() {
    val viewModel by lazy { ViewModelProvider(this)[WeatherViewModel::class.java] }

    lateinit var binding: ActivityWeatherBinding

    //    private lateinit var binding:ActivityWeatherBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navBtn: Button = findViewById(R.id.navBtn)
        navBtn.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)//打开滑动菜单
        }
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {

            }

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(
                    drawerView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }

            override fun onDrawerStateChanged(newState: Int) {

            }

        })

        val decorView = window.decorView
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT

        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: "116.405285"
            Log.d("lng", viewModel.locationLng)
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: "39.904989"
        }

        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: "中国 北京市 北京"
        }
        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
                Log.d("there", "observe")
            } else {
                Toast.makeText(this, "无法获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            binding.swipeRefresh.isRefreshing = false
        })

        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        refreshWeather()
        binding.swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }
    }

    fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        val swipeRefresh: SwipeRefreshLayout = findViewById(R.id.swipeRefresh)
        swipeRefresh.isRefreshing = true
    }

    private fun showWeatherInfo(weather: Weather) {
        val placeName = findViewById<TextView>(R.id.placeName)
        placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily

        //填充now布局
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        val currentTemp = findViewById<TextView>(R.id.currentTemp)
        val currentSky = findViewById<TextView>(R.id.currentSky)
        val currentPM25 = findViewById<TextView>(R.id.currentAQI)
        val nowLayout = findViewById<RelativeLayout>(R.id.nowLayout)

        currentTemp.text = currentTempText
        currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数${realtime.airQuality.aqi.chn.toInt()}"
        currentPM25.text = currentPM25Text
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

        //填充forecast布局
        val forecastLayout = findViewById<LinearLayout>(R.id.forecastLayout)
        forecastLayout.removeAllViews()
        val days = daily.skycon.size//未来几?天
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view =
                LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false)
            var dateinfo: TextView = view.findViewById(R.id.dateInfo)
            val skyIcon: ImageView = view.findViewById(R.id.skyIcon)
            val skyInfo: TextView = view.findViewById(R.id.skyInfo)
            val temperatureInfo: TextView = view.findViewById(R.id.temperatureInfo)

            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateinfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyInfo.text = sky.info
            skyIcon.setImageResource(sky.icon)
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            forecastLayout.addView(view)
        }

        //填充life_index布局
        val lifeIndex = daily.lifeIndex
        var coldRiskText: TextView = findViewById(R.id.coldRiskText)
        val dressingText: TextView = findViewById(R.id.dressingText)
        val ultravioletText: TextView = findViewById(R.id.ultravioletText)
        val carWashingText: TextView = findViewById(R.id.carWashingText)
        val weatherLayout: ScrollView = findViewById(R.id.weatherLayout)

        coldRiskText.text = lifeIndex.coldRisk[0].desc
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        weatherLayout.visibility = View.VISIBLE

    }
}