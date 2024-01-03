package com.example.kotlinmovieapp.presentation.components

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
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.kotlinmovieapp.presentation.browse.BrowseState
import com.example.kotlinmovieapp.presentation.browse.Types

@Composable
fun Filters(
    state: State<BrowseState>
) {
    var opened by remember {
        mutableStateOf("")
    }
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ){
        TextButton(onClick = {
            opened = "type"
        }) {
            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")
            Text(text = state.value.type.title)
        }
        TextButton(onClick = {
            opened = "catalog"
        }) {
            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")
            Text(text = state.value.catalog.title)
        }
        TextButton(onClick = {
            opened = "genre"
        }) {
            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")
            Text(text = state.value.genre?.name ?: "genre")
        }


    }

    when(opened) {
        "type" -> FullScreenDialog(showDialog = true, onDismiss = {opened = ""}, title = "type") {
            Types.forEach { 
                ListItem(headlineContent = { Text(text = it.title) })
            }
        }
        "catalog" -> FullScreenDialog(showDialog = true, onDismiss = {opened = ""} , title = "catalog") {
            state.value.type.catalog.forEach {
                ListItem(headlineContent = { Text(text = it.title) })
            }
        }
        "genre" -> FullScreenDialog(showDialog = true, onDismiss = {opened = ""}, title = "genre") {
            Text(text = "Genres")
//            ListItem(headlineContent = { Text(text = it.title) })
        }
    }

}