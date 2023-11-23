package com.example.kotlinmovieapp.presentation.account

import android.content.Intent
import android.net.Uri
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun Account (
    viewModel: AccountViewModel
) {
    viewModel.getReqToken()
    val context = LocalContext.current
    val state = viewModel.state.value
    val url = "https://www.themoviedb.org/authenticate/${state.token}"
    Text(text = "Account")
    Button(onClick = {
        if (state.token != null) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
    }) {
        Text(text = "Login")
    }
}
