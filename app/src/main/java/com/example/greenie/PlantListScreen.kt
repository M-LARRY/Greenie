package com.example.greenie

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil3.compose.AsyncImage
import com.example.greenie.model.Plant
import com.example.greenie.model.Search
import com.example.greenie.network.ApiClient
import com.example.greenie.ui.theme.GreenieTheme
import com.google.firebase.auth.FirebaseAuth
import io.moyuru.cropify.Cropify
import io.moyuru.cropify.CropifyOption
import io.moyuru.cropify.CropifySize.PercentageSize.Companion.FullSize
import io.moyuru.cropify.rememberCropifyState
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID

sealed interface PlantsQueryState {
    data class Success(val plants: List<Plant>) : PlantsQueryState
    data class Error(val message: String) : PlantsQueryState
    data object Loading : PlantsQueryState
}

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun PlantListScreen(
    auth : FirebaseAuth,
    latitude: Double,
    longitude: Double,
    brightness: Float,
    onNavigateToSomething: () -> Unit
) {
    var plantsQueryState by remember { mutableStateOf<PlantsQueryState>(PlantsQueryState.Loading) }
    var showDialog by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf("") }
    var pictureTaken by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val cropifyState = rememberCropifyState()
    var croppedImage by remember { mutableStateOf<ImageBitmap?>(null) }

    val context: Context = LocalContext.current

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                pictureTaken = true
            }
        }
    )

    val scope = rememberCoroutineScope()

    fun createImageFile(): File {
        // Create an image file in the app's cache directory
        val storageDir = File(context.cacheDir, "images")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        return File.createTempFile("JPEG_${UUID.randomUUID()}_", ".jpg", storageDir)
    }

    LaunchedEffect(Unit) {
        plantsQueryState = try {
            PlantsQueryState.Success(
                ApiClient.retrofit.searchPlants(
                    auth.currentUser!!.getIdToken(false).await().token!!,
                    latitude,
                    longitude,
                    brightness
                )
            )
        } catch (e: Exception) {
            PlantsQueryState.Error(e.message ?: "Unknown error")
        }
    }

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
                                showDialog = true
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
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
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
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = {showDialog = false},
                    title = { Text("Save this search") },
                    text = {
                        Column {
                            if (pictureTaken) {
                                Cropify(
                                    modifier = Modifier
                                        .fillMaxHeight(0.5f)
                                        .fillMaxWidth(),
                                    uri = imageUri!!,
                                    state = cropifyState,
                                    onImageCropped = { croppedImage = it},
                                    onFailedToLoadImage = {},
                                    option = CropifyOption(
                                        frameSize = FullSize
                                    ),
                                )
                                TextButton(
                                    onClick = {
                                        cropifyState.crop()
                                    },
                                ) {
                                    Text("Crop")
                                }
                            } else {
                                TextButton(
                                    onClick = {
                                        createImageFile().let { file ->
                                            imageUri = FileProvider.getUriForFile(
                                                context,
                                                "com.example.greenie.fileprovider",
                                                file
                                            )
                                            takePictureLauncher.launch(imageUri!!)
                                        }
                                    },
                                ) {
                                    Text("Take a picture")
                                }
                            }
                            TextField(
                                value = textFieldValue,
                                onValueChange = { textFieldValue = it },
                                label = { Text("Name for the search") },
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            enabled = croppedImage != null,
                            onClick = {
                                scope.launch {
                                    ApiClient.retrofit.saveSearch(
                                        auth.currentUser!!.getIdToken(false).await().token!!,
                                        auth.currentUser!!.uid,
                                        Search(
                                            name = textFieldValue,
                                            lng = longitude,
                                            lat = latitude,
                                            brightness = brightness,
                                            picture = croppedImage?.asAndroidBitmap()?.convertToBase64()
                                        )
                                    )
                                    showDialog = false
                                }
                            }
                        ) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun PlantsList(
    plants: List<Plant>
) {
    LazyVerticalGrid(
        GridCells.Adaptive(minSize = 192.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        items(plants) { plant ->
            PlantItem(plant = plant)
        }
    }
}

@Composable
fun PlantItem(
    plant: Plant,
) {
    Log.d("RES", plant.imgUrl)
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier.padding(4.dp),
    ) {
        Column(
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
                modifier = Modifier.padding(
                    paddingValues = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 0.dp,
                        bottom = 16.dp
                    )
                )
            )
        }
    }
}
