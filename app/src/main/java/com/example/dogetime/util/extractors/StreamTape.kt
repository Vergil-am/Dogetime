package com.example.dogetime.util.extractors

import com.example.dogetime.domain.model.Source

class StreamTape {
    suspend fun getVideo(url: String): Source {

        return Source(
            url = "",
            quality = "",
            label = "",
            source = "",
            header = ""
        )
    }
}