package com.example.kotlinmovieapp.util.extractors.vidsrcto.model

import com.example.kotlinmovieapp.domain.model.Source
import com.example.kotlinmovieapp.util.extractors.vidplay.models.Subtitle

data class VidsrctoReturnType(
    val sources : List<Source>,
    val subtitles : List<Subtitle>

)
