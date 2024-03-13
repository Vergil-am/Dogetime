package com.example.kotlinmovieapp.presentation.components

import android.content.Intent
import android.net.Uri
import android.util.Log
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
import com.example.kotlinmovieapp.domain.model.Source
import com.example.kotlinmovieapp.util.extractors.vidplay.models.Subtitle

@Composable
fun Source(
    source: Source,
    title: String,
    subtitles: List<Subtitle>?,
    onClick: () -> Unit
) {
    val intent = Intent(Intent.ACTION_VIEW)
    val context = LocalContext.current
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp), onClick = {
//        val url = URLEncoder.encode(source.link)


        // This is specific to MX player
        intent.setDataAndType(Uri.parse(source.url), "video/*")
//        if (header != null) {
//            intent.putExtra("headers", arrayOf("Referer", state))
//        }
        intent.putExtra("title", title)
        Log.e("Subtitles", subtitles.toString())
        intent.putExtra("subs", subtitles?.map { Uri.parse(it.file) }?.toTypedArray())
        intent.putExtra("subs.name", subtitles?.map { it.label }?.toTypedArray())

        Log.e("Extras", intent.extras.toString())

        context.startActivity(intent)

        onClick()
    }) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)

        ) {
            Text(text = source.source)
            Column {
                Text(text = source.label)
            }
            Icon(
                imageVector = Icons.Filled.PlayArrow, contentDescription = "play"
            )
        }
    }
}