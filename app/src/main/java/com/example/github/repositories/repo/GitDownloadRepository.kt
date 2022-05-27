package com.example.github.repositories.repo

import android.content.Context
import com.example.github.repositories.data.ORDER
import com.example.github.repositories.data.QUERY
import com.example.github.repositories.data.RepositoryDTO
import com.example.github.repositories.data.SORT
import com.example.github.repositories.data.source.GitHubRepoApi
import kotlinx.coroutines.flow.*

class GitDownloadRepository private constructor(
) {

    private val _repositoryError = MutableSharedFlow<String?>()
    val repositoryError = _repositoryError.conflate()

    val gitHubRepoListFlow get() = _gitHubRepoListFlow.filterNotNull()
    private val _gitHubRepoListFlow = MutableSharedFlow<MutableList<RepositoryDTO>?>()

    companion object {
        @Volatile
        private var instance: GitDownloadRepository? = null

        fun getInstance(): GitDownloadRepository {
            return instance ?: synchronized(GitDownloadRepository::class.java) {
                if (instance == null) {
                    instance = GitDownloadRepository()
                }
                return instance!!
            }
        }
    }
    suspend fun executeSearchRepositories(){
        try {
            val response = GitHubRepoApi.retrofitService.searchRepositories(QUERY, SORT, ORDER)
            _gitHubRepoListFlow.emit(response?.items)
        }catch (e: Exception){
            _repositoryError.emit(e.message)
        }
    }
}