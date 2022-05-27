package com.example.github.repositories.ui.main_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.github.repositories.data.*
import com.example.github.repositories.repo.GitDownloadRepository
import kotlinx.coroutines.*

class MainViewModel : ViewModel() {
    val gitDownloadRepository = GitDownloadRepository.getInstance()
    val networkScope = CoroutineScope(Dispatchers.IO)
    val repositories = gitDownloadRepository.gitHubRepoListFlow.asLiveData()
    val error = gitDownloadRepository.repositoryError.asLiveData()

    fun fetchItems() {
        networkScope.coroutineContext.cancelChildren()
        networkScope.launch {
            delay(1_000)
            gitDownloadRepository.executeSearchRepositories()
        }
//        CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
//            delay(1_000) // This is to simulate network latency, please don't remove!
//            repositories.postValue(service.searchRepositories(QUERY, SORT, ORDER).execute().body()?.items)
//        }
    }

    fun refresh() {
        networkScope.coroutineContext.cancelChildren()
        networkScope.launch {
            delay(1_000)
            gitDownloadRepository.executeSearchRepositories()
        }
//        GlobalScope.launch(Dispatchers.Main) {
//            delay(1_000) // This is to simulate network latency, please don't remove!
//            var response: Response?
//            withContext(Dispatchers.IO) {
//                response = service.searchRepositories(QUERY, SORT, ORDER).execute().body()
//            }
//            repositories.value = response?.items
//        }
    }
}