package com.example.kotlinmovieapp.presentation.account

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.kotlinmovieapp.datastore.AccountStore
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun Account (
    viewModel: AccountViewModel
) {
    val context = LocalContext.current
    val dataStore = AccountStore(context)
    val state = viewModel.state.value

//    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
//        result -> if (result.resultCode == Activity.RESULT_CANCELED) {
//        state.token?.let { viewModel.getSessionId(it, dataStore) }
//    }
//    }


    Box(modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
//
//    if (state.accountId == null && state.sessionId == null) {
//                Button(onClick = {
//                    val token = viewModel.getReqToken()
//                    token.invokeOnCompletion {
//                        if (it == null) {
//                            state.token = token.getCompleted().request_token
//                            val url =
//                                "https://www.themoviedb.org/authenticate/${token.getCompleted().request_token}"
//                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//                            launcher.launch(intent)
//                        }
//                    }
//                }) {
//                    Text(text = "Login")
//                }
//
//
//
//        } else {
//            Text(text = "You are logged in")
//        }

    }

}
