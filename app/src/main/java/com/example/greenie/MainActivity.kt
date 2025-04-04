package com.example.greenie

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.greenie.navigation.Route
import com.example.greenie.network.ApiClient
import com.example.greenie.ui.theme.GreenieTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.ricknout.composesensors.light.rememberLightSensorValueAsState

class MainActivity : ComponentActivity() {

    // Private variables to store location data
    private var latitude by mutableDoubleStateOf(0.0)
    private var longitude by mutableDoubleStateOf(0.0)
    private var brightness by mutableFloatStateOf(0f) // Store brightness as a float
    private var locationString by mutableStateOf("")
    private var locationFound by mutableStateOf(false)

    // Private variable to handle location updates
    private lateinit var locationHelper: LocationHelper

    private lateinit var auth: FirebaseAuth

    // Function to request coarse location permission
    private fun requestCoarseLocationPermission() {
        // Check if the permission has already been granted
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                102 // Use a unique requestCode for coarse location
            )
        }
    }

    // Function to start location updates
    @RequiresPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
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
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestCoarseLocationPermission()
        locationHelper = LocationHelper(this) { location ->
            latitude = location.latitude
            longitude = location.longitude
            locationFound = true
        }

        setContent {
            GreenieTheme {

                LaunchedEffect(latitude, longitude) {
                    val nations = ApiClient.retrofit.searchNations(latitude, longitude)
                    if (nations.isNotEmpty()) {
                        locationString = nations[0].name
                    }
                }

                // Use rememberLightSensorValueAsState().value.value to get the brightness value
                brightness = rememberLightSensorValueAsState().value.value
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = Route.SignIn) {
                    composable<Route.Home> { HomeScreen(
                        brightness = brightness,
                        location = locationString,
                        locationFound = locationFound,
                        onNavigateToPlantsListPage = {
                            navController.navigate(Route.PlantList(
                                lat = latitude.toFloat(),
                                lng = longitude.toFloat(),
                                brightness = brightness
                            ))
                        },
                        onNavigateToSavedListPage = {
                            navController.navigate(Route.SavedList)
                        },
                    ) }
                    composable<Route.PlantList> { backStackEntry ->
                        val plantList: Route.PlantList = backStackEntry.toRoute()
                        PlantListScreen(
                            latitude = plantList.lat.toDouble(),
                            longitude = plantList.lng.toDouble(),
                            brightness = plantList.brightness,
                            onNavigateToSomething = {}
                        )
                    }
                    composable<Route.SavedList> { SavedListScreen(
                        onNavigateToSearch = { latitude: Double, longitude: Double, brightness: Float ->
                            navController.navigate(Route.PlantList(
                                lat = latitude.toFloat(),
                                lng = longitude.toFloat(),
                                brightness = brightness,
                                )
                            )
                        }
                    ) }
                    composable<Route.SignIn> { SignInScreen(navController, auth) }
                    composable<Route.SignUp> { SignUpScreen(navController, auth) }
                    // Add more destinations similarly.
                }
            }
        }
    }
}