package com.example.kotlinmovieapp.presentation.browse

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.kotlinmovieapp.presentation.navgraph.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Browse(
    navController: NavController
) {
    var isOpen by remember {
        mutableStateOf(false)
    }
    var selected by remember {
        mutableStateOf("Movies")
    }
    var selectedGenre by remember {
        mutableStateOf("All")
    }
   Scaffold (
       topBar = {
           TopAppBar(
               title = { Text(text = "LOGO")},
               actions = {
                   IconButton(onClick = {
                       navController.navigate(Route.Search.route)
                   }) {
                       Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search icon")
                   }
               }
               )
       }
   ) { paddingValues ->
       Surface (
           modifier = Modifier.padding(paddingValues)
       ) {
           Row {
               Box(modifier = Modifier) {
                   OutlinedButton(onClick = {isOpen = !isOpen}) {
                       Text(text = selected)
                       Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")
                   }
                   DropdownMenu(
                       expanded = isOpen,
                       onDismissRequest = {
                           isOpen = !isOpen
                       }) {
                       DropdownMenuItem(text = {
                           Text(text = "Movies")
                       }, onClick = {
                           selected = "Movies"
                           isOpen = !isOpen
                       })
                       DropdownMenuItem(text = {
                           Text(text = "Series")
                       }, onClick = {
                           selected = "Series"
                           isOpen = !isOpen
                       })
                   }

               }
               Box(modifier = Modifier) {
                   OutlinedButton(onClick = {isOpen = !isOpen}) {
                       Text(text = selected)
                       Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")
                   }
                   DropdownMenu(
                       expanded = isOpen,
                       onDismissRequest = {
                           isOpen = !isOpen
                       }) {
                       DropdownMenuItem(text = {
                           Text(text = "Movies")
                       }, onClick = {
                           selected = "Movies"
                           isOpen = !isOpen
                       })
                       DropdownMenuItem(text = {
                           Text(text = "Series")
                       }, onClick = {
                           selected = "Series"
                           isOpen = !isOpen
                       })
                   }

               }
               Box(modifier = Modifier) {
                   OutlinedButton(onClick = {isOpen = !isOpen}) {
                       Text(text = selected)
                       Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")
                   }
                   DropdownMenu(
                       expanded = isOpen,
                       onDismissRequest = {
                           isOpen = !isOpen
                       }) {
                       DropdownMenuItem(text = {
                           Text(text = "Action")
                       }, onClick = {
                           selected = "Movies"
                           isOpen = !isOpen
                       })
                   }

               }

           }

       }
   }
}


