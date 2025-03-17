package com.example.greenie

import android.content.pm.PackageManager
import android.os.Bundle
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

class MainActivity : ComponentActivity() {

    // Private variables to store location data
    private var latitude by mutableDoubleStateOf(0.0)
    private var longitude by mutableDoubleStateOf(0.0)
    private var brightness by mutableFloatStateOf(0f) // Store brightness as a float

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestCoarseLocationPermission()
        locationHelper = LocationHelper(this) { location ->
            latitude = location.latitude
            longitude = location.longitude
        }
        setContent {
            GreenieTheme {
                // Use rememberLightSensorValueAsState().value.value to get the brightness value
                brightness = rememberLightSensorValueAsState().value.value
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        brightness = brightness,
                        latitude = latitude,
                        longitude = longitude,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(
    brightness: Float,
    latitude: Double,
    longitude: Double,
    modifier: Modifier = Modifier
) {

    Text(
        text = "Brightness: $brightness,\n" +
                "Latitude: $latitude,\n" +
                "Longitude: $longitude",
        modifier = modifier
    )
}