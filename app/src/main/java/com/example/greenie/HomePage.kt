package com.example.greenie

import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp


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
    onNavigateToPlantsListPage: () -> Unit,
) {

    @Composable
    fun contextGetter() : Context {
        return LocalContext.current
    }

    fun onClick(context: Context) {
        var res = queryFun(context)
        if (res != null) {
            onNavigateToPlantsListPage()
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
                .fillMaxWidth()
                .padding(16.dp),
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
    onNavigateToPlantsListPage: () -> Unit
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
            )
            QueryButton(
                queryFun = queryFun,
                enabled = true,
                waitingResponse = waitingResponse,
                onNavigateToPlantsListPage = onNavigateToPlantsListPage,
            )
        }
    }
}