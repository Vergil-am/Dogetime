package com.example.kotlinmovieapp.local.repository

import com.example.kotlinmovieapp.local.dao.ListDao

class ListRepository (
    private val list : ListDao
){
    // TODO

    suspend fun getAllLists() : List<com.example.kotlinmovieapp.local.entities.List> {
        return list.getLists()
    }

    suspend fun createList(newList: com.example.kotlinmovieapp.local.entities.List) {
        return list.createList(newList)
    }

}