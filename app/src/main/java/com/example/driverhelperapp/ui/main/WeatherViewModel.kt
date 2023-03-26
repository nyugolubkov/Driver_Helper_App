package com.example.driverhelperapp.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.driverhelperapp.BaseViewModel
import com.example.driverhelperapp.CoroutinesErrorHandler
import com.example.driverhelperapp.R
import com.example.driverhelperapp.models.Location
import com.example.driverhelperapp.models.WeatherEntity
import com.example.driverhelperapp.repositories.WeatherRepository
import com.example.driverhelperapp.utils.ApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
): BaseViewModel() {

    private val _weather = MutableLiveData<ApiResponse<WeatherEntity>>()
    val weather = _weather

    private val imageMap = mapOf(
        "01d" to R.raw.d_one,
        "02d" to R.raw.d_two,
        "03d" to R.raw.d_three,
        "04d" to R.raw.d_four,
        "09d" to R.raw.d_nine,
        "10d" to R.raw.d_ten,
        "11d" to R.raw.d_eleven,
        "13d" to R.raw.d_thirteen,
        "50d" to R.raw.d_fifty,
        "01n" to R.raw.n_one,
        "02n" to R.raw.n_two,
        "03n" to R.raw.n_three,
        "04n" to R.raw.n_four,
        "09n" to R.raw.n_nine,
        "10n" to R.raw.n_ten,
        "11n" to R.raw.n_eleven,
        "13n" to R.raw.n_thirteen,
        "50n" to R.raw.n_fifty
    )

    private var location: Location? = null

    private fun getWeather(
        fake: Boolean, lat: String, lon: String,
        coroutinesErrorHandler: CoroutinesErrorHandler)  = baseRequest(
        _weather,
        coroutinesErrorHandler
    ) {
        weatherRepository.getWeather(fake, lat, lon)
    }

    fun getWeatherImage(inputString: String): Int? {
        return imageMap[inputString]
    }

    fun setLocation(lat: Double, lon: Double, context: Context) {
        this.location = Location(lat=lat, lon=lon)

        if (location != null) {
            getWeather(
                fake = false,
                lat = location!!.lat.toString(),
                lon = location!!.lon.toString(),
                object : CoroutinesErrorHandler {
                    @SuppressLint("SetTextI18n")
                    override fun onError(message: String) {
                        Toast.makeText(
                            context,
                            "Ошибка! $message",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }
    }

    fun getLocation(): Location? {
        return location
    }
}