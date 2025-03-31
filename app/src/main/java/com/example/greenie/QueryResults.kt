package com.example.greenie

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.greenie.ui.theme.GreenieTheme
import dev.ricknout.composesensors.light.rememberLightSensorValueAsState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QueryResults : ComponentActivity() {


    // onCreate function to initialize the activity
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GreenieTheme {
                val scrollBehavior =
                    TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
                // Use rememberLightSensorValueAsState().value.value to get the brightness value
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
                        Text("CULO")
                    }
                }
            }
        }
    }
}