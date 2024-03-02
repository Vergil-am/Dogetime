package com.example.kotlinmovieapp.util.extractors.vidplay.models

data class Payload(
    val allShortcutsEnabled: Boolean,
    val blob: Blob,
    val copilotAccessAllowed: Boolean,
    val copilotInfo: Any,
    val currentUser: Any,
    val fileTreeProcessingTime: Double,
    val foldersToFetch: List<Any>,
    val path: String,
    val refInfo: Any,
    val repo: Any,
    val symbolsExpanded: Boolean,
    val treeExpanded: Boolean
)