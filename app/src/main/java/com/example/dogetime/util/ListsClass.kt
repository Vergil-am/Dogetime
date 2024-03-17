package com.example.dogetime.util

sealed class ListsClass(
    val name: String, val value: String
) {
    companion object {
        val lists: List<ListsClass> = listOf(Watching, Planning, Completed, Dropped, Paused)
    }

    object Watching : ListsClass("Watching", "watching")
    object Planning : ListsClass("Planning", "planning")
    object Completed : ListsClass("Completed", "completed")
    object Dropped : ListsClass("Dropped", "dropped")
    object Paused : ListsClass("Paused", "paused")
    object All : ListsClass("All", "all")


}