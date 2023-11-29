package com.example.kotlinmovieapp.presentation.account

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.kotlinmovieapp.datastore.AccountStore
import kotlinx.coroutines.launch

@Composable
fun Account (
    viewModel: AccountViewModel
) {
    viewModel.getReqToken()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = AccountStore(context)
    val state = viewModel.state.value
    val url = "https://www.themoviedb.org/authenticate/${state.token}"

    Column {
        if (state.sessionId != null) {
            Text(text = state.sessionId)
        } else {
            Text(text = "Account")
        }
        Button(onClick = {
            if (state.token != null) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
        }) {
            Text(text = "Login")
        }
        Button(onClick = {
            state.token?.let { viewModel.getSessionId(it) }
            state.sessionId?.let { scope.launch {
                dataStore.storeSessionId(it)
            }}
        }) {
            Text(text = "Get session id")
        }
        
        Button(onClick = {
            
        }) {
           Text(text = "Get account id") 
        }

    }

}
