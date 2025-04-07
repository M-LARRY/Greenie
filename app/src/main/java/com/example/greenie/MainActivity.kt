package com.example.greenie

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.greenie.model.Location
import com.example.greenie.navigation.Route
import com.example.greenie.network.ApiClient
import com.example.greenie.ui.theme.GreenieTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.ricknout.composesensors.light.rememberLightSensorValueAsState
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {

    // Private variables to store location data
    private var hasLocationPermission by mutableStateOf(false)
    private var location by mutableStateOf(Location())
    private var brightness by mutableFloatStateOf(0f) // Store brightness as a float
    private var locationString by mutableStateOf("")

    // Private variable to handle location updates
    private lateinit var locationHelper: LocationHelper

    private lateinit var auth: FirebaseAuth

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            hasLocationPermission =
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
        }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activityResultLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        } else {
            hasLocationPermission = true
        }
    }

    // onCreate function to initialize the activity
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        locationHelper = LocationHelper(this) { loc ->
            location = Location(loc)
        }

        checkLocationPermission()

        setContent {
            GreenieTheme {
                LifecycleStartEffect(hasLocationPermission) {
                    if (hasLocationPermission) locationHelper.startLocationUpdates()
                    onStopOrDispose {
                        locationHelper.stopLocationUpdates()
                    }
                }

                LaunchedEffect(location, auth.currentUser) {
                    if (auth.currentUser == null) return@LaunchedEffect

                    val nations =
                        ApiClient.retrofit.searchNations(auth.currentUser!!.getIdToken(false).await().token!!, location.latitude, location.longitude)
                    if (nations.isNotEmpty()) {
                        locationString = nations[0].name
                    }
                }

                // Use rememberLightSensorValueAsState().value.value to get the brightness value
                brightness = rememberLightSensorValueAsState().value.value
                val navController = rememberNavController()

                val startDestination: Route = if (auth.currentUser != null) Route.Home else Route.SignIn

                NavHost(navController = navController, startDestination = startDestination) {
                    composable<Route.Home> {
                        HomeScreen(
                            brightness = brightness,
                            location = locationString,
                            locationFound = location.found(),
                            onNavigateToPlantsListPage = {
                                navController.navigate(
                                    Route.PlantList(
                                        lat = location.latitude.toFloat(),
                                        lng = location.longitude.toFloat(),
                                        brightness = brightness
                                    )
                                )
                            },
                            onNavigateToSavedListPage = {
                                navController.navigate(Route.SavedList)
                            },
                        )
                    }
                    composable<Route.PlantList> { backStackEntry ->
                        val plantList: Route.PlantList = backStackEntry.toRoute()
                        PlantListScreen(
                            auth = auth,
                            latitude = plantList.lat.toDouble(),
                            longitude = plantList.lng.toDouble(),
                            brightness = plantList.brightness,
                            onNavigateToSomething = {}
                        )
                    }
                    composable<Route.SavedList> {
                        SavedListScreen(
                            auth = auth,
                            onNavigateToSearch = { latitude: Double, longitude: Double, brightness: Float ->
                                navController.navigate(
                                    Route.PlantList(
                                        lat = latitude.toFloat(),
                                        lng = longitude.toFloat(),
                                        brightness = brightness,
                                    )
                                )
                            }
                        )
                    }
                    composable<Route.SignIn> { SignInScreen(navController, auth) }
                    composable<Route.SignUp> { SignUpScreen(navController, auth) }
                    // Add more destinations similarly.
                }
            }
        }
    }
}