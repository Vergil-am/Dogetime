package com.example.dogetime.domain.use_case.mycima

import com.example.dogetime.domain.model.Details
import com.example.dogetime.domain.model.Source

data class MyCimaDetails(
    val details: Details,
    val sources: List<Source>
)
