package com.example.github.repositories.ui.user_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.github.repositories.repo.GitDownloadRepository
import kotlinx.coroutines.*

class UserViewModel : ViewModel() {

    val gitDownloadRepositoryScope = CoroutineScope(Dispatchers.IO)
    val fetchUserScope = CoroutineScope(Dispatchers.IO)

    val gitDownloadRepository = GitDownloadRepository.getInstance()

    val repositories = gitDownloadRepository.gitHubRepoListFlow.asLiveData()
    val repositoryNetworkFetchError = gitDownloadRepository.repositoryNetworkFetchError.asLiveData()
    val getUserNetworkFetchError = gitDownloadRepository.getUserNetworkFetchError.asLiveData()

    val user = gitDownloadRepository.getUserFlow.asLiveData()

    fun fetchUser(username: String?) {
        fetchUserScope.coroutineContext.cancelChildren()
        fetchUserScope.launch {
            delay(1_000)// This is to simulate network latency, please don't remove!
            gitDownloadRepository.executeGetUser(username)
        }
    }

    fun fetchRepositories(reposUrl: String) {
        gitDownloadRepositoryScope.coroutineContext.cancelChildren()
        gitDownloadRepositoryScope.launch {
            delay(1_000)// This is to simulate network latency, please don't remove!
            gitDownloadRepository.executeGetUserRepositories(reposUrl)
        }
    }
}