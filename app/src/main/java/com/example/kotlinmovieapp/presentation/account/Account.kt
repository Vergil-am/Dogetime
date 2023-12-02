package com.example.kotlinmovieapp.presentation.account

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.kotlinmovieapp.datastore.AccountStore
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun Account (
    viewModel: AccountViewModel
) {
    val context = LocalContext.current
    //val scope = rememberCoroutineScope()
    val dataStore = AccountStore(context)
    val state = viewModel.state.value

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
        result -> if (result.resultCode == Activity.RESULT_CANCELED) {
        state.token?.let { viewModel.getSessionId(it, dataStore) }
    }
    }

    if (state.account == null) {
        Column {
            if (state.sessionId != null) {
                Text(text = state.sessionId!!)
            } else {
                Text(text = "Account")
            }
            Button(onClick = {
                val token = viewModel.getReqToken()
                token.invokeOnCompletion {
                    if (it == null) {
                        state.token = token.getCompleted().request_token
                        val url = "https://www.themoviedb.org/authenticate/${token.getCompleted().request_token}"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        launcher.launch(intent)
                    }
                }
            }) {
                Text(text = "Login")
            }

            Text(text = "session ID = ${state.sessionId}")

        }

    } else {
        val account = state.account
        if (account != null) {
            Column {
                Text(text = account.name)
                Text(text = account.id.toString())
                Text(text = account.username)
            }

        }
    }



}
