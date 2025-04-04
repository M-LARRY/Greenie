package com.example.greenie

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.greenie.model.Plant
import com.example.greenie.model.Search
import com.example.greenie.network.ApiClient
import com.example.greenie.ui.theme.GreenieTheme
import kotlinx.coroutines.launch

sealed interface PlantsQueryState {
    data class Success(val plants: List<Plant>) : PlantsQueryState
    data class Error(val message: String) : PlantsQueryState
    data object Loading : PlantsQueryState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantListScreen (
    latitude: Double,
    longitude: Double,
    brightness: Float,
    onNavigateToSomething: () -> Unit
) {
    var plantsQueryState by remember { mutableStateOf<PlantsQueryState>(PlantsQueryState.Loading) }

    val scope = rememberCoroutineScope()

    var loading by remember { mutableStateOf(true) }
    var plants = listOf<Plant>()

    // Carica i dati qui dentro ------------------
    LaunchedEffect(Unit) {
        //DEBUG-----
        if (true) {
            plantsQueryState = PlantsQueryState.Success(debugOfflinePlants())
            return@LaunchedEffect
        }
        //DEBUG-----

        plantsQueryState = try {
            PlantsQueryState.Success(ApiClient.retrofit.searchPlants(latitude, longitude, brightness))
        } catch (e: Exception) {
            PlantsQueryState.Error(e.message ?: "Unknown error")
        }
    }
    // ------------------

    GreenieTheme {
        val scrollBehavior =
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
        Scaffold(
            topBar = {
                MediumTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text(
                            "These plants will thrive!",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    actions = {
                        if (plantsQueryState != PlantsQueryState.Loading) {
                            IconButton(onClick = {
                                scope.launch {
                                    ApiClient.retrofit.saveSearch(
                                        "pippo",
                                        Search(
                                            name = "test-search",
                                            lng = longitude,
                                            lat = latitude,
                                            brightness = brightness
                                        )
                                    )
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.FavoriteBorder,
                                    contentDescription = "Localized description"
                                )
                            }
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
        ) {
            innerPadding ->
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)) {
                when (plantsQueryState) {
                    is PlantsQueryState.Loading ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    is PlantsQueryState.Success -> {
                        val plants = (plantsQueryState as PlantsQueryState.Success).plants
                        PlantsList(
                            plants = plants,
                        )
                    }
                    is PlantsQueryState.Error -> {
                        val message = (plantsQueryState as PlantsQueryState.Error).message
                        Text(text = "Error: $message")
                    }
                }
            }
        }
    }
}


@Composable
fun PlantsList(
    plants : List<Plant>
) {
    LazyVerticalGrid(
        GridCells.Adaptive(minSize = 192.dp),
        modifier = Modifier.padding(8.dp)
    )  {
        items(plants.size) { index ->
            PlantItem(plant = plants[index])
        }
    }
}

@Composable
fun PlantItem(
    plant: Plant,
) {
    Card (
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier.padding(4.dp),
    ) {
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = plant.imgUrl,
                contentDescription = null,
            )
            Text(
                plant.name,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                plant.description,
                modifier = Modifier.padding(paddingValues = PaddingValues(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 16.dp))
            )
        }
    }
}

fun debugOfflinePlants() : List<Plant> {
    val imgUrl = "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fi.redd.it%2Fqi0r0pdbsgs31.jpg&f=1&nofb=1&ipt=5f8a4ee80e9c5a811382aa23493afb3d421ff40148b491426cc07051508d37a5&ipo=images"
    val plants = listOf(
        Plant(
            name = "Potato",
            description = "Boil em, mash em, stick em in a stew",
            imgUrl = imgUrl,
            nations = listOf("Italy", "India")
        ),
        Plant(
            name = "Potato",
            description = "Boil em, mash em, stick em in a stew",
            imgUrl = imgUrl,
            nations = listOf("Italy", "India")
        ),
        Plant(
            name = "AAAAAAAAAAAAAAA",
            description = "Boil em, mash em, stick em in a stew",
            imgUrl = imgUrl,
            nations = listOf("Italy", "India")
        ),
    )
    return plants
}