package com.example.dogetime.presentation.components

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
import com.example.dogetime.presentation.browse.BrowseViewModel
import com.example.dogetime.presentation.browse.Item
import com.example.dogetime.presentation.browse.Type
import com.example.dogetime.presentation.browse.Types

@Composable
fun Filters(
    viewModel: BrowseViewModel
) {
    var opened by remember {
        mutableStateOf("")
    }
    val state by viewModel.state.collectAsState()
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround
    ) {
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

    FullScreenDialog(showDialog = opened == "type", onDismiss = { opened = "" }, title = "type") {
        Types.forEach {
            ListItem(modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = {
                    viewModel.updateType(
                        Type(
                            title = it.title,
                            value = it.value,
                            catalog = it.catalog,
                            genres = it.genres
                        ), catalog = it.catalog[0]
                    )

                    opened = ""
                }), headlineContent = {
                Text(
                    text = it.title
                )
            })
        }
    }
    FullScreenDialog(showDialog = opened == "catalog",
        onDismiss = { opened = "" },
        title = "catalog"
    ) {
        state.type.catalog.forEach {
            ListItem(modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = {
                    viewModel.updateCatalog(it)
                    opened = ""
                }), headlineContent = {
                Text(text = it.title)
            })
        }
    }
    FullScreenDialog(
        showDialog = opened == "genre", onDismiss = { opened = "" }, title = "genre"
    ) {
        state.type.genres.forEach {
            ListItem(modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = {
                    if (state.type.value == "anime") {
                        viewModel.updateCatalog(Item("Type", "type"))
                        viewModel.updateGenre(it)
                    } else {
                        viewModel.updateGenre(it)
                    }
                    opened = ""
                }), headlineContent = { Text(text = it.name) })
        }
    }
}
