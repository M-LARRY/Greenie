package com.example.greenie

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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

sealed interface SearchesQueryState {
    data class Success(val searches: List<Search>) : SearchesQueryState
    data class Error(val message: String) : SearchesQueryState
    data object Loading : SearchesQueryState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedListScreen (
    onNavigateToSomething: () -> Unit
) {
    var searchesQueryState by remember { mutableStateOf<SearchesQueryState>(SearchesQueryState.Loading) }

    LaunchedEffect(Unit) {
        searchesQueryState = try {
            SearchesQueryState.Success(ApiClient.retrofit.getSearches("userId"))
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
        ) {
            innerPadding ->
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)) {
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
                        SavedList(
                            searches = searches,
                        )
                    }
                    is SearchesQueryState.Error -> {
                        val message = (searchesQueryState as SearchesQueryState.Error).message
                        Text(text = "Error: ${message}")
                    }
                }
            }
        }
    }
}


@Composable
fun SavedList(
    searches: List<Search>
) {
    LazyVerticalGrid(
        GridCells.Adaptive(minSize = 192.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        items(searches.size) {
            for (search in searches) {
                SavedItem(search = search)
            }
        }
    }
}

@Composable
fun SavedItem(
    search: Search,
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
            Text(
                search.lat.toString(),
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                search.lng.toString(),
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                search.brightness.toString(),
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineSmall,
            )
        }
    }
}