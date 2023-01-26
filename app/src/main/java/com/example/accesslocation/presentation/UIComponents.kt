package com.example.accesslocation.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import com.example.accesslocation.presentation.data.WeatherApi
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.style.TextAlign
import  androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.example.accesslocation.R
import com.example.accesslocation.presentation.theme.AccesslocationTheme


@Composable
fun ButtonWidget(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        Button(modifier = Modifier.size(ButtonDefaults.LargeButtonSize),
            onClick = { onClick()}) {
            Icon(
                imageVector = Icons.Rounded.LocationOn,
                contentDescription = "Llama la solicitud de ubicación",
                modifier = iconModifier
            )
        }
    }
}

@Composable
fun TextComponent(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = text
    )
}

@Composable
fun CardComponent(
    modifier: Modifier = Modifier, title: String, weatherDes: String, time: String,
    temp: Double
) {
    AppCard(onClick = { /*TODO*/ }, appName = { Text("Clima", color = Color.White) },
        time = { Text(time, color = if (temp < 12) Color.White else Color.Red) },
        title = { Text(title, color = Color.Yellow) }) {
        val icon = if (temp < 12) R.mipmap.high else R.mipmap.low

        Row(horizontalArrangement = Arrangement.Center) {
            Image(
                modifier = Modifier.height(25.dp),
                painter = painterResource(id = icon),
                contentDescription = "",
            )

            Spacer(modifier = Modifier.size(7.dp))
            Text(weatherDes)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable

fun  WearApp(locationUtil: LocationManager){
    var listState = rememberScalingLazyListState()
    AccesslocationTheme{
        val locationPermissionState =
            rememberMultiplePermissionsState(
                listOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
            )
        val contentModifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
        val iconModifier = Modifier
            .size(24.dp)
            .padding()
            .wrapContentSize(align = Alignment.Center)
        ScalingLazyColumn(modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 32.dp,
            start = 8.dp,
            end =  8.dp,
            bottom = 32.dp,
        ), verticalArrangement = Arrangement.Bottom,
        state =  listState,
        autoCentering =  AutoCenteringParams(0)
        ){
            item{Spacer(modifier =  Modifier.size(20.dp))}
            if(locationPermissionState.allPermissionsGranted){
                if(!locationUtil.dataLoaded.value){
                    item{ TextComponent(contentModifier, "Acceso a la ubicación Concedido")}
                }
                else{
                    item{
                        CardComponent(
                            modifier = contentModifier,
                            title = locationUtil.data.value.name,
                            weatherDes = locationUtil.data.value.weatherInfo,
                            time = locationUtil.data.value.time,
                            temp = locationUtil.data.value.temp,)
                    }
                }
            }
            else{
                val allpermissions = locationPermissionState.permissions.size==
                        locationPermissionState.revokedPermissions.size
                val textToShow = if(!allpermissions){
                    "Concede permisos a la ubicación exacta por favor "
                } else if(locationPermissionState.shouldShowRationale){
                    "La ubicación exacta es necesaria"
                }
                else{
                    "Para el correcto funcionamiento de la App concedad permisos"
                }
                item { TextComponent(contentModifier, textToShow) }
                item { ButtonWidget(contentModifier,iconModifier) {
                    locationPermissionState.launchMultiplePermissionRequest()

                } }
            }
        }
    }

}

class UIComponents {
}