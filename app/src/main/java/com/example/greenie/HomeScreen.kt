package com.example.greenie

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    brightness: Float,
    location: String,
    locationFound: Boolean,
    onNavigateToPlantsListPage: () -> Unit,
    onNavigateToSavedListPage: () -> Unit
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
                    IconButton(onClick = {
                        onNavigateToSavedListPage()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.FavoriteBorder,
                            contentDescription = "Localized description"
                        )
                    }
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
            Button(
                onClick = onNavigateToPlantsListPage,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Text("Find compatible plants!")
            }
        }
    }
}

@Composable
fun BarChart(value: Float = 100f, maxValue : Float = 7000f) {
    val barLength = (value / maxValue).coerceIn(0f, 1f)

    val level = when {
        value < 500 -> "Dark"
        value < 1500 -> "Shade"
        value < 3000 -> "Half-Shade"
        value < 6000 -> "Partial Sun"
        else -> "Full Sun"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row (
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Brightness level: ",
                modifier = Modifier.padding(vertical = 3.dp),
            )
            Text(
                text = level,
                style = MaterialTheme.typography.headlineMedium
            )
        }

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
    if (locationFound) {
        Text("Current location:\n $location")
    }
    else {
        Text("Retrieving your current location...")
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
    }
}

