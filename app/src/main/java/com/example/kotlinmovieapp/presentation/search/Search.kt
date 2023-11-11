package com.example.kotlinmovieapp.presentation.search

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kotlinmovieapp.presentation.navgraph.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(
    navController: NavController
) {
    var searchValue by remember {
        mutableStateOf("")
    }
    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(text = "")},
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Route.Browse.route)
                    }) {
                        Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "Search" )
                    }
                    OutlinedTextField(
                        value = searchValue,
                        placeholder = { Text(text = "Search")},
                        singleLine = true,
                        trailingIcon = { Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search Icon" )},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                        ,
                        onValueChange = {value -> searchValue = value})

                }
            )
        }

    ) { paddingValues ->
        Surface (
            modifier = Modifier.padding(paddingValues)
        ) {
        }
    }
}

