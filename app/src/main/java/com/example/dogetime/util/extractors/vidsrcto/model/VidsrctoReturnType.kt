package com.example.dogetime.util.extractors.vidsrcto.model

import com.example.dogetime.domain.model.Source
import com.example.dogetime.util.extractors.vidplay.models.Subtitle

data class VidsrctoReturnType(
    val sources : List<Source>,
    val subtitles : List<Subtitle>

)
