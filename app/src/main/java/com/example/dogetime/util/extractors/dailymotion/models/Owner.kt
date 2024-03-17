package com.example.dogetime.util.extractors.dailymotion.models

data class Owner(
    val avatars: Avatars,
    val id: String,
    val screenname: String,
    val type: String,
    val url: String,
    val username: String,
    val watermark_image_url: Any,
    val watermark_link_url: Any
)