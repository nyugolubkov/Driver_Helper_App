package com.example.driverhelperapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import com.example.driverhelperapp.ui.main.WeatherViewModel
import com.google.android.gms.location.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.TimeUnit

class LocationProvider (
    private val appContext: Activity,
    private val client: FusedLocationProviderClient,
    private val permReqLauncher: ActivityResultLauncher<Array<String>>,
    private val viewModel: WeatherViewModel
    ) {

    companion object {
        var PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    @SuppressLint("MissingPermission")
    fun fetchLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                val locationRequest = LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    TimeUnit.SECONDS.toMillis(2)
                ).apply {
                    setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                    setWaitForAccurateLocation(true)
                }.build()

                val callBack = object : LocationCallback() {
                    @SuppressLint("SetTextI18n")
                    override fun onLocationResult(locationResult: LocationResult) = runBlocking {
                        super.onLocationResult(locationResult)
                        val location = locationResult.lastLocation
                        if (location != null) {
                            try {
                                viewModel.setLocation(location.latitude, location.longitude, appContext)
                                appContext.findViewById<TextView>(R.id.localityTextView).text =
                                    getDeviceCityName(location.latitude, location.longitude) + " " +
                                            location.latitude.toString() + " " +
                                            location.longitude.toString()
                            } catch(_: Exception) {
                            }
                        }
                    }
                }

                client.requestLocationUpdates(locationRequest, callBack, Looper.getMainLooper())
            } else {
                Toast.makeText(appContext, "Пожалуйста, разрешите геолокацию", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                appContext.startActivity(intent)
            }
        } else {
            permReqLauncher.launch(PERMISSIONS)
        }
    }

    private fun getDeviceCityName(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(appContext, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(
            latitude,
            longitude,
            5
        ) as List<Address>
        return addresses[0].locality
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
}