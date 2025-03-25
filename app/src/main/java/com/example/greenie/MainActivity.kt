package com.example.greenie

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.greenie.ui.theme.GreenieTheme
import dev.ricknout.composesensors.light.rememberLightSensorValueAsState
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.annotation.RequiresPermission
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // Private variables to store location data
    private var latitude by mutableDoubleStateOf(0.0)
    private var longitude by mutableDoubleStateOf(0.0)
    private var brightness by mutableFloatStateOf(0f) // Store brightness as a float
    private var locationString by mutableStateOf("")
    private var locationFound by mutableStateOf(false)
    private var waitingResponse by mutableStateOf(false)

    // Private variable to handle location updates
    private lateinit var locationHelper: LocationHelper

    // Function to request coarse location permission
    fun requestCoarseLocationPermission() {
        // Check if the permission has already been granted
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                102 // Use a unique requestCode for coarse location
            )
        }
    }

    fun determineLocation(latitude: Double, longitude: Double) : String{

        return latitude.toString() + longitude.toString()
    }

    fun queryFun(
    ) {
        var timeout = 3000L
        waitingResponse = true
        Log.d("debug", "Query $latitude, $longitude, $brightness")
        // TODO: logic to connect to API here!
        var response : Map<String, Int> = mapOf(
            "0" to 0,
            "1" to 1,
            "2" to 2
            )
        GlobalScope.launch {
            delay(timeout) // Suspends the coroutine for 3 seconds

            // Code to execute after the delay
            waitingResponse = false
        }
        return
    }

    // Function to start location updates
    @RequiresPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
    override fun onResume() {
        super.onResume()
        // Start listening for location updates when the activity resumes
        locationHelper.startLocationUpdates()
    }

    // Function to stop location updates when the activity pauses
    override fun onPause() {
        super.onPause()
        // Stop listening for location updates when the activity pauses
        locationHelper.stopLocationUpdates()
    }

    // onCreate function to initialize the activity
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestCoarseLocationPermission()
        locationHelper = LocationHelper(this) { location ->
            latitude = location.latitude
            longitude = location.longitude
            locationString = determineLocation(latitude, longitude)
            locationFound = true
        }

        setContent {
            GreenieTheme {
                val scrollBehavior =
                    TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
                // Use rememberLightSensorValueAsState().value.value to get the brightness value
                brightness = rememberLightSensorValueAsState().value.value
                Scaffold(
                    topBar = {
                        LargeTopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = {
                                Text(
                                    "Greenie",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            actions = {
                                IconButton(onClick = { /* do something */ }) {
                                    Icon(
                                        imageVector = Icons.Filled.MoreVert,
                                        contentDescription = "Localized description"
                                    )
                                }
                            },
                            scrollBehavior = scrollBehavior
                        )
                    },
                ) { innerPadding ->
                    Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        SensorPanel(
                            brightness = brightness,
                            location = locationString,
                            locationFound = locationFound,
                            queryFun = ::queryFun,
                            waitingResponse = waitingResponse,
                            modifier = Modifier.padding(innerPadding),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BarChart(value: Float = 100f) {
    var maxValue = 7000f
    var barLength = 0f
    if (value < maxValue){
        barLength = (value / maxValue)
    }
    else {
        barLength = 1f
    }
    Log.d("Bar length", "${barLength * 100}%")
    var level = ""
    if (value < 500){
        level = "Dark"
    }
    else if (value < 1500){
        level = "Shade"
    }
    else if (value < 3000){
        level = "Half-Shade"
    }
    else if (value < 6000){
        level = "Partial Sun"
    }
    else {
        level = "Full Sun"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Brightness level: $level",
            modifier = Modifier
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x40ffeb62))
        ) {
            // Animated Box for the bar
            val animatedWidth by animateFloatAsState(
                targetValue = barLength,
                animationSpec = tween(durationMillis = 300) // Add a duration for the animation
            )

            Box(
                modifier = Modifier
                    .animateContentSize() // This animates the container size change
                    .height(64.dp)
                    .fillMaxWidth(fraction = animatedWidth) // Animate width based on calculated barLength
                    .background(Color(0xffffeb62))
            )
        }
    }
}

@Composable
fun LocationElement(
    location: String,
    locationFound: Boolean,
) {
    if (locationFound == true) {
        Text("Current location:\n " + location)
    }
    else {
        Text("Retrieving your current location...")
    }
}

@Composable
fun QueryButton(
    queryFun: () -> Unit,
    enabled: Boolean,
    waitingResponse: Boolean,
) {
    fun onClick() {
        queryFun()
        Log.d("debug", "searching compatible plants...")
    }
    if (waitingResponse) {
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth(),
            enabled = enabled
        ) {
            Text("Loading...")
        }
    }
    else {
        Button(
            onClick = { onClick() },
            modifier = Modifier
                .fillMaxWidth(),
            enabled = enabled
        ) {
            Text("Find compatible plants!")
        }
    }
}

@Composable
fun SensorPanel(
    brightness: Float,
    location: String,
    locationFound: Boolean,
    queryFun: () -> Unit,
    waitingResponse: Boolean,
    modifier: Modifier
) {
    Column (modifier = Modifier.padding(all = 16.dp)) {
        BarChart(value = brightness)
        LocationElement(location = location, locationFound = locationFound)
        HorizontalDivider(thickness = 16.dp, color = Color(0x00000000))
        QueryButton(queryFun = queryFun, enabled = locationFound, waitingResponse = waitingResponse)
    }
}