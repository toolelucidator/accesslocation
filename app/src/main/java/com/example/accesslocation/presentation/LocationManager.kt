package com.example.accesslocation.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import com.example.accesslocation.presentation.data.WeatherApi
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class LocationManager {
    val dataLoaded = mutableStateOf(false)

    val data = mutableStateOf(
        CardData(
            weatherInfo = "",
            time = "", name = "", temp = 0.0
        )
    )

    fun createLocationRequest(
        context: Context,
        fusedLocationclient:
        FusedLocationProviderClient
    ) {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY, 1000
        ).build()
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationclient.requestLocationUpdates(
            locationRequest, object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    for (location in p0.locations) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val weatherDTO = WeatherApi.apiInstance.getWeatherDetails(
                                location.latitude,
                                location.longitude, "4b6c4c7f96c37f40ff9b1f0cdc220e19"
                            )
                            dataLoaded.value = true
                            data.value = CardData(
                                name = weatherDTO.name,
                                time = "${(weatherDTO.main.temp - 273).roundToInt()}°C",
                                weatherInfo = weatherDTO.weather[0].description,
                                temp = (weatherDTO.main.temp - 273).roundToInt().toDouble()
                            )


                        }
                    }
                }
            }, Looper.getMainLooper()
        )

    }

}