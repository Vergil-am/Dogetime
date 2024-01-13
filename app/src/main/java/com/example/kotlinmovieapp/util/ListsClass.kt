package com.example.kotlinmovieapp.util

sealed class ListsClass(
    val name: String, val value: String
) {
    object Watching : ListsClass("Watching", "watching")
    object Planning : ListsClass("Planning", "planning")
    object Completed : ListsClass("Completed", "completed")
    object Dropped : ListsClass("Dropped", "dropped")
    object Paused : ListsClass("Paused", "paused")


    val lists = listOf(Watching, Planning, Completed, Dropped, Paused)
}