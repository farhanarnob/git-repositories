package com.example.github.repositories.data

import com.example.github.repositories.repo.GitDownloadRepository

class LocalDataStore private constructor() {

    companion object {
        @Volatile
        private var instance: LocalDataStore? = null
        fun getInstance(): LocalDataStore {
            return instance ?: synchronized(LocalDataStore::class.java) {
                if (instance == null) {
                    instance = LocalDataStore()
                }
                return instance!!
            }
        }
    }

    private val bookmarks = mutableMapOf<Int, RepositoryDTO>()

    fun bookmarkRepo(repositoryDTO: RepositoryDTO, bookmarked: Boolean) {
        if (bookmarked)
            bookmarks[repositoryDTO.id!!] = repositoryDTO
        else
            bookmarks.remove(repositoryDTO.id)
    }

    fun getBookmarks() = bookmarks.values
}