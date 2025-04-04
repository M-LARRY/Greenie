package com.example.greenie

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.greenie.model.Search
import com.example.greenie.ui.theme.GreenieTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedListScreen (
    onNavigateToSearch: () -> Unit
) {

    var loading by remember { mutableStateOf(true) }
    var searches = listOf<Search>()

    // Carica i dati qui dentro ------------------
    LaunchedEffect(Unit) {
        //DEBUG-----
        searches = debugOfflineSearches() // DEBUG
        //-----
//        val response = ApiClient.retrofit.getSearches("pippo")
//
//        Log.d("debug", response.toString())
//
//        if (response.isSuccessful && response.body() != null) {
//            Log.d("debug", response.body().toString())
//            searches = response.body()!!
//        } else {
//            Log.d("debug", response.errorBody().toString())
//        }
        loading = false
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
                SearchesList(
                    searches = searches,
                    loading = loading
                )
            }
        }
    }
}


@Composable
fun SearchesList(
    searches: List<Search>,
    loading: Boolean
) {
    if (loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    else {
        LazyVerticalGrid(
            GridCells.Adaptive(minSize = 192.dp),
            modifier = Modifier.padding(8.dp)
        )  {
            items(searches.size) {
                for (search in searches) {
                    SearchItem(search = search)
                }
            }
        }
    }
}

@Composable
fun SearchItem(
    search: Search,
) {

    val scope = rememberCoroutineScope()

    Card (
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier.padding(4.dp),
        onClick = {

        },
    ) {
        Column {
            Text(
                search.lat.toString(),
                modifier = Modifier.padding(paddingValues = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp))
            )
            Text(
                search.lng.toString(),
                modifier = Modifier.padding(paddingValues = PaddingValues(horizontal = 16.dp))
            )
            Text(
                search.brightness.toString(),
                modifier = Modifier.padding(paddingValues = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp))
            )
        }
    }
}

fun debugOfflineSearches() : List<Search> {
    val searches = listOf(
        Search(
            lat = 41.0,
            lng = 42.0,
            brightness = 5000f
        ),
        Search(
            lat = 41.0,
            lng = 42.0,
            brightness = 5000f
        ),
        Search(
            lat = 41.0,
            lng = 42.0,
            brightness = 5000f
        ),
        Search(
            lat = 41.0,
            lng = 42.0,
            brightness = 5000f
        ),
    )
    return searches
}

