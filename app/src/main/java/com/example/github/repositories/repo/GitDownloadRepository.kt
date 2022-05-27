package com.example.github.repositories.repo

import com.example.github.repositories.data.*
import com.example.github.repositories.data.source.GitHubRepoApi
import kotlinx.coroutines.flow.*

class GitDownloadRepository private constructor(
) {

    private val _repositoryNetworkFetchError = MutableSharedFlow<String?>()
    val repositoryNetworkFetchError = _repositoryNetworkFetchError.conflate()

    private val _getUserNetworkFetchError = MutableSharedFlow<String?>()
    val getUserNetworkFetchError = _getUserNetworkFetchError.conflate()

    val gitHubRepoListFlow get() = _gitHubRepoListFlow.filterNotNull()
    private val _gitHubRepoListFlow = MutableSharedFlow<MutableList<RepositoryDTO>?>()

    val getUserFlow get() = _getUserFlow.filterNotNull()
    private val _getUserFlow = MutableSharedFlow<UserDTO?>()

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
            _repositoryNetworkFetchError.emit(e.message)
        }
    }

    suspend fun executeGetUserRepositories(userRepo: String){
        try {
            val response = GitHubRepoApi.retrofitService.getUserRepositories(userRepo)
            _gitHubRepoListFlow.emit(response)
        }catch (e: Exception){
            _repositoryNetworkFetchError.emit(e.message)
        }
    }

    suspend fun executeGetUser(username: String?){
        try {
            val response = GitHubRepoApi.retrofitService.getUser(username)
            _getUserFlow.emit(response)
        }catch (e: Exception){
            _getUserNetworkFetchError.emit(e.message)
        }
    }
}