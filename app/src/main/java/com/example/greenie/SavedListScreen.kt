package com.example.greenie

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.greenie.model.Search
import com.example.greenie.network.ApiClient
import com.example.greenie.ui.theme.GreenieTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

sealed interface SearchesQueryState {
    data class Success(val searches: List<Search>) : SearchesQueryState
    data class Error(val message: String) : SearchesQueryState
    data object Loading : SearchesQueryState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedListScreen(
    auth : FirebaseAuth,
    onNavigateToSearch: (Double, Double, Float) -> Unit
) {
    var searchesQueryState by remember { mutableStateOf<SearchesQueryState>(SearchesQueryState.Loading) }

    LaunchedEffect(Unit) {
        //DEBUG-----
        if (false) {
            searchesQueryState = SearchesQueryState.Success(debugOfflineSearches())
            return@LaunchedEffect
        }
        //DEBUG-----

        searchesQueryState = try {
            SearchesQueryState.Success(ApiClient.retrofit.getSearches(auth.currentUser!!.getIdToken(false).await().token!!, auth.currentUser!!.uid))
        } catch (e: Exception) {
            SearchesQueryState.Error(e.message ?: "Unknown error")
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
                            "Your saved results",
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (searchesQueryState) {
                    is SearchesQueryState.Loading ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }

                    is SearchesQueryState.Success -> {
                        val searches = (searchesQueryState as SearchesQueryState.Success).searches
                        SearchesList(
                            searches = searches,
                            onClick = onNavigateToSearch
                        )
                    }

                    is SearchesQueryState.Error -> {
                        val message = (searchesQueryState as SearchesQueryState.Error).message
                        Text(text = "Error: $message")
                    }
                }
            }
        }
    }
}

@Composable
fun SearchesList(
    searches: List<Search>,
    onClick: (Double, Double, Float) -> Unit
) {
    LazyVerticalGrid(
        GridCells.Adaptive(minSize = 192.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        items(searches) { search ->
            SearchItem(search = search, onClick = onClick)
        }
    }
}

@Composable
fun SearchItem(
    search: Search,
    onClick: (Double, Double, Float) -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier.padding(4.dp),
        onClick = {
            onClick(search.lat, search.lng, search.brightness)
        },
    ) {
        Column {
            Text(
                search.name,
                modifier = Modifier.padding(
                    paddingValues = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 8.dp
                    )
                )
            )
            Text(
                search.lat.toString(),
                modifier = Modifier.padding(
                    paddingValues = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 8.dp
                    )
                )
            )
            Text(
                search.lng.toString(),
                modifier = Modifier.padding(paddingValues = PaddingValues(horizontal = 16.dp))
            )
            Text(
                search.brightness.toString(),
                modifier = Modifier.padding(
                    paddingValues = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 16.dp
                    )
                )
            )
        }
    }
}

fun debugOfflineSearches(): List<Search> {
    val searches = listOf(
        Search(
            name = "test-search-1",
            lat = 41.0,
            lng = 42.0,
            brightness = 5000f
        ),
        Search(
            name = "test-search-2",
            lat = 141.0,
            lng = 142.0,
            brightness = 8000f
        ),
        Search(
            name = "test-search-3",
            lat = 41.0,
            lng = 42.0,
            brightness = 5000f
        ),
    )
    return searches
}