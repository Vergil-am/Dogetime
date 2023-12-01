package com.example.kotlinmovieapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinmovieapp.data.remote.dto.ShowDetailsDTO

@Composable
fun ShowInfo(
    show: ShowDetailsDTO
) {

    Text(text = show.tagline,
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight(300),
            color = Color.Gray,
            textAlign = TextAlign.Center
        ),
        modifier = Modifier
            .fillMaxWidth()
    )
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
        ,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "TV Show")
        Text(text = show.first_air_date.split("-")[0])
        Text(text = "${show.episode_run_time} min")
        Text(text = show.vote_average.toString().format("%.f"))
    }
    Text(text = show.overview,
        modifier = Modifier.padding(10.dp)
    )
    Row (
        modifier = Modifier
            .fillMaxWidth()
        ,
        horizontalArrangement = Arrangement.spacedBy(8.dp)

    ) {
        show.genres.forEach{
                genre -> AssistChip(
            onClick = {},
            label = { Text(text = genre.name) }
        )
        }
    }
}
