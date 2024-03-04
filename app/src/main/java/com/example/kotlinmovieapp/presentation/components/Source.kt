package com.example.kotlinmovieapp.presentation.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.kotlinmovieapp.util.extractors.vidplay.models.Subtitle
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Source(
    source: String, info: String, link: String, header: String?, subtitles : List<Subtitle>?, title: String , onClick: () -> Unit
) {
    val intent = Intent(Intent.ACTION_VIEW)
val context = LocalContext.current
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp), onClick = {
        val url = URLEncoder.encode(link)


        // This is specific to MX player
        intent.setDataAndType(Uri.parse(url), "video/*")
        intent.putExtra("title", title)
        intent.putExtra(Intent.EXTRA_REFERRER, header)
        intent.putExtra("subs", subtitles?.map{ it.file }?.toTypedArray())
        intent.putExtra("subs.name", subtitles?.map { it.label }?.toTypedArray())
        context.startActivity(intent)


        onClick()
    }) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)

        ) {
            Text(text = source)
            Column {
                Text(text = info)
            }
            Icon(
                imageVector = Icons.Filled.PlayArrow, contentDescription = "play"
            )
        }
    }
}