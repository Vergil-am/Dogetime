package com.example.kotlinmovieapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.kotlinmovieapp.presentation.browse.BrowseViewModel
import com.example.kotlinmovieapp.presentation.browse.Types

@Composable
fun Filters(
    viewModel: BrowseViewModel
) {
    var opened by remember {
        mutableStateOf("")
    }
    val state = viewModel.state.collectAsState().value
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ){
        TextButton(onClick = {
            opened = "type"
        }) {
            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")
            Text(text = state.type.title)
        }
        TextButton(onClick = {
            opened = "catalog"
        }) {
            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")
            Text(text = state.catalog.title)
        }
        TextButton(onClick = {
            opened = "genre"
        }) {
            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")
            Text(text = state.genre?.name ?: "genre")
        }


    }

    when(opened) {
        "type" -> FullScreenDialog(showDialog = true, onDismiss = {opened = ""}, title = "type") {
            Types.forEach { 
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = {
                            viewModel.getMovies(it.value, page = 1, catalog = state.catalog.value)
                            state.type = it
                            state.catalog = it.catalog[0]
                            state.genre = null
                            opened = ""
                    }),
                    headlineContent = {
                        Text(
                            text = it.title
                        )
                })
            }
        }
        "catalog" -> FullScreenDialog(showDialog = true, onDismiss = {opened = ""} , title = "catalog") {
            state.type.catalog.forEach {
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = {
                            viewModel.getMovies(state.type.value, it.value, 1)
                            state.catalog = it
                            opened = ""
                        }),
                    headlineContent = {
                        Text(text = it.title)
                    }
                )
            }
        }
        "genre" -> FullScreenDialog(
            showDialog = true,
            onDismiss = {opened = ""},
            title = "genre") {
            state.type.genres.forEach {
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable (
                            onClick = {
                                state.genre = it
//                                TODO Fetch based on genre

                                opened = ""
                            }
                        )
                    ,
                    headlineContent = { Text(text = it.name) }
                )
            }
        }
    }

}