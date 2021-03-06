package com.example.github.repositories.repo

import com.example.github.repositories.data.*
import com.example.github.repositories.data.source.GitHubRepoApi
import kotlinx.coroutines.flow.*

class GitDownloadRepository private constructor(
) {

    private val _networkFetchError = MutableSharedFlow<String?>()
    val networkFetchError = _networkFetchError.asSharedFlow()

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
            if(!response?.items.isNullOrEmpty()){
                _gitHubRepoListFlow.emit(response?.items)
            }else{
                _networkFetchError.emit("No repository found")
            }
        }catch (e: Exception){
            _networkFetchError.emit(e.message)
        }
    }

    suspend fun executeGetUserRepositories(userRepo: String?){
        try {
            val response = GitHubRepoApi.retrofitService.getUserRepositories(userRepo)
            if(!response.isNullOrEmpty()){
                _gitHubRepoListFlow.emit(response)
            }else{
                _networkFetchError.emit("No repository found")
            }
        }catch (e: Exception){
            _networkFetchError.emit(e.message)
        }
    }

    suspend fun executeGetUser(username: String?){
        try {
            val response = GitHubRepoApi.retrofitService.getUser(username)
            _getUserFlow.emit(response)
        }catch (e: Exception){
            _networkFetchError.emit(e.message)
        }
    }
}