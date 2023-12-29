package com.example.kotlinmovieapp.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.kotlinmovieapp.presentation.components.DMCA
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Account (
    viewModel: SettingsViewModel
) {
    var opened by remember {
        mutableStateOf(false)
    }
    val state = viewModel.state.value
    Column (
        modifier = Modifier.fillMaxSize()
    ) {

        Card (
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(10.dp),
            onClick = {opened = true},
        )
        {
            Row (
                modifier = Modifier
                    .fillMaxSize()
                ,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Disclaimer"
                )
                Text(text = "Disclaimer")
            }

        }
        Text(text = state.version)

    }


   if (opened) {
       ModalBottomSheet(
           onDismissRequest = {opened = false},
           modifier = Modifier.padding(10.dp)
       ) {
           Text(text = "Disclaimer",
               modifier = Modifier
                   .fillMaxWidth(),
               textAlign = TextAlign.Center,
               style = MaterialTheme.typography.titleLarge
               )
           DMCA()

       }
   }

}
