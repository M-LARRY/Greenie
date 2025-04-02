package com.example.greenie

import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.greenie.model.Search
import com.example.greenie.network.ApiClient
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.await
import kotlinx.serialization.Serializable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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

    private lateinit var auth: FirebaseAuth

    private var initialRoute : String = "login"

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
            
            if (response.isSuccessful) {
                Log.d("debug", response.body().toString())
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

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null)
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.d("debug", "User is already signed in")
            initialRoute = "home"
        }
    }

    // onCreate function to initialize the activity
    @OptIn(ExperimentalMaterial3Api::class)
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

                NavHost(navController = navController, startDestination = initialRoute) {
                    composable("home") { HomePage(
                        brightness = brightness,
                        location = locationString,
                        locationFound = locationFound,
                        queryFun = ::queryFun,
                        waitingResponse = waitingResponse,
                        navController = navController
                    ) }
                    composable("login") { SigninScreen(auth) }
                    composable("signup") { SignupScreen(auth) }
                    composable("plantslist") { QueryResults() }
                    // Add more destinations similarly.
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
    queryFun: (context: Context) -> String,
    enabled: Boolean,
    waitingResponse: Boolean,
    navController: NavController
) {

    @Composable
    fun contextGetter() : Context{
        return LocalContext.current
    }

    fun onClick(context: Context) {
        var res = queryFun(context)
        if (res != null) {
            Log.d("QUERYFUN", res.toString())
            navController.navigate("plantslist")
        }
    }

    val context = contextGetter()
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
            onClick = { onClick(context) },
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
    modifier: Modifier
) {
    Column (modifier = Modifier.padding(all = 16.dp)) {
        BarChart(value = brightness)
        LocationElement(location = location, locationFound = locationFound)
        HorizontalDivider(thickness = 16.dp, color = Color(0x00000000))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    brightness: Float,
    location: String,
    locationFound: Boolean,
    queryFun: (context: Context) -> String,
    waitingResponse: Boolean,
    navController: NavController
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
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
                location = location,
                locationFound = locationFound,
                modifier = Modifier.padding(innerPadding),
            )
            QueryButton(
                queryFun = queryFun,
                enabled = true,
                waitingResponse = waitingResponse,
                navController = navController
            )
        }
    }
}
