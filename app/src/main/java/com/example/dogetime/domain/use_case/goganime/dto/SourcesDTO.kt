package com.example.dogetime.domain.use_case.goganime.dto

data class SourcesDTO(
    val advertising: List<Any>,
    val linkiframe: String,
    val source: List<Source>,
    val source_bk: List<Source>,
    val track: List<Any>
)