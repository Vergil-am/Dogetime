package com.example.kotlinmovieapp.presentation.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.kotlinmovieapp.R
import com.example.kotlinmovieapp.presentation.components.DMCA
import com.example.kotlinmovieapp.presentation.components.FullScreenDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Account(
    viewModel: SettingsViewModel
) {
    val context = LocalContext.current
    viewModel.getTheme(context)
    var chosen by remember {
        mutableStateOf("")
    }
    val state by viewModel.state.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(10.dp),
            onClick = {
                chosen = "disclaimer"
            },
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
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
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(10.dp),
            onClick = {
                chosen = "theme"
            },
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Image(
                    painter = painterResource(id = R.drawable.theme),
                    contentDescription = "Theme",
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                Text(text = "Theme")


            }

        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(10.dp),
            onClick = {
                chosen = "backup"
            },
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sync),
                    contentDescription = "Restore",
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                Text(text = "Backup & Restore")


            }

        }
        Text(text = state.version)

    }

    FullScreenDialog(
        showDialog = chosen == "disclaimer", onDismiss = { chosen = "" }, title = "Disclaimer"
    ) {
        DMCA()
    }
    FullScreenDialog(showDialog = chosen == "theme", onDismiss = { chosen = "" }, title = "Theme") {
        val themes = listOf("dark", "light", "system")
        themes.forEach {
            DropdownMenuItem(text = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "check",
                        modifier = Modifier.alpha(
                            if (it == state.theme) {
                                1f
                            } else {
                                0.0f
                            }
                        )
                    )
                    Text(text = it, modifier = Modifier.padding(horizontal = 10.dp))
                }
            }, onClick = {
                viewModel.setTheme(context, it)
                state.theme = it
            })
        }


    }

    FullScreenDialog(
        showDialog = chosen == "backup", onDismiss = { chosen = "" }, title = "Backup & Restore"
    ) {
        DropdownMenuItem(text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.restore),
                    contentDescription = "Restore",
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                Text(text = "Backup", modifier = Modifier.padding(horizontal = 10.dp))
            }
        }, onClick = { })
        DropdownMenuItem(text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.restore),
                    contentDescription = "Restore",
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                Text(text = "Restore", modifier = Modifier.padding(horizontal = 10.dp))
            }
        }, onClick = { })
    }


}
