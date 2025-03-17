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
import androidx.compose.ui.tooling.preview.Preview
import com.example.greenie.ui.theme.GreenieTheme
import dev.ricknout.composesensors.light.rememberLightSensorValueAsState
import android.location.Location
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.util.Log
import androidx.annotation.RequiresPermission

class LocationExample(private val context: Context, private  val onLocationChangedCB: (Location) -> Unit) {

    private var locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var listener: MyLocationListener

    init {
        listener = MyLocationListener()
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_COARSE_LOCATION])
    fun startLocationUpdates() {
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, // Provider (GPS or Network)
            1000, // Minimum time between updates in milliseconds (1 second)
            1f, // Minimum distance between updates in meters (1 meter)
            listener
        )
    }

    fun stopLocationUpdates() {
        locationManager.removeUpdates(listener)
    }

    // Define your LocationListener implementation
    inner class MyLocationListener : android.location.LocationListener {
        override fun onLocationChanged(location: Location) {
            onLocationChangedCB(location)
            val latitude = location.latitude
            val longitude = location.longitude
            Log.d("GPS-->", "New Location: Latitude - $latitude, Longitude - $longitude")
            // Use the location data as needed
        }

        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }
}

class MainActivity : ComponentActivity() {

    private var latitude by mutableStateOf(0.0)
    private var longitude by mutableStateOf(0.0)
    private lateinit var locationExample: LocationExample

    fun requestCoarseLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                102 // Use a unique requestCode for coarse location
            )
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    override fun onResume() {
        super.onResume()
        // Start listening for location updates when the activity resumes
        locationExample.startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        // Stop listening for location updates when the activity pauses
        locationExample.stopLocationUpdates()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestCoarseLocationPermission()
        locationExample = LocationExample(this) { location ->
            Log.d("GPS-->", "Updating location...")
            latitude = location.latitude
            longitude = location.longitude
        }
        setContent {
            GreenieTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding),
                        latitude = latitude,
                        longitude = longitude
                    )
                }
            }
        }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, latitude: Double, longitude: Double) {

    val brightness by rememberLightSensorValueAsState()

    Text(
        text = "Brightness: ${brightness.value}, Latitude: $latitude, Longitude: $longitude",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GreenieTheme {
        Greeting(name = "Android", latitude = 0.0, longitude = 0.0)
    }
}