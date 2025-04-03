package com.example.greenie

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
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
import com.example.greenie.model.Plant
import com.example.greenie.network.ApiClient
import com.example.greenie.ui.theme.GreenieTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.ricknout.composesensors.light.rememberLightSensorValueAsState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object HomePage

@Serializable
object PlantsListPage

@Serializable
object SigninPage

@Serializable
object SignupPage

class MainActivity : ComponentActivity() {

    // Private variables to store location data
    private var latitude by mutableDoubleStateOf(0.0)
    private var longitude by mutableDoubleStateOf(0.0)
    private var brightness by mutableFloatStateOf(0f) // Store brightness as a float
    private var locationString by mutableStateOf("")
    private var locationFound by mutableStateOf(false)
    private var waitingResponse by mutableStateOf(false)
    private var plants by mutableStateOf(listOf<Plant>())

    // Private variable to handle location updates
    private lateinit var locationHelper: LocationHelper

    private lateinit var auth: FirebaseAuth

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
        context: Context
    ) : String {
        var timeout = 3000L
        waitingResponse = true
        Log.d("debug", "Query $latitude, $longitude, $brightness")
        // TODO: logic to connect to API here!

        GlobalScope.launch {
            val response = ApiClient.retrofit.searchPlants(latitude, longitude, brightness)

            Log.d("debug", response.toString())

            if (response.isSuccessful && response.body() != null) {
                Log.d("debug", response.body().toString())
                plants = response.body()!!
            } else {
                Log.d("debug", response.errorBody().toString())
            }

//            val response2 = ApiClient.retrofit.saveSearch("testUser", Search(longitude, latitude, brightness)).await()
//
//            Log.d("debug", response2.toString())

            // Code to execute after the delay
            waitingResponse = false
        }

        return "OK"
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
        auth = Firebase.auth;
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

                // Use rememberLightSensorValueAsState().value.value to get the brightness value
                brightness = rememberLightSensorValueAsState().value.value
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = SigninPage) {
                    composable<HomePage> { HomeScreen(
                        brightness = brightness,
                        location = locationString,
                        locationFound = locationFound,
                        queryFun = ::queryFun,
                        waitingResponse = waitingResponse,
                        onNavigateToPlantsListPage = {
                            navController.navigate(PlantsListPage)
                        }
                    ) }
                    composable<PlantsListPage> { PlantListScreen(
                        plants,
                        onNavigateToSomething = {}
                    ) }
                    composable<SigninPage> { SigninScreen(navController, auth) }
                    composable<SignupPage> { SignupScreen(navController, auth) }
                    // Add more destinations similarly.
                }
            }
        }
    }
}