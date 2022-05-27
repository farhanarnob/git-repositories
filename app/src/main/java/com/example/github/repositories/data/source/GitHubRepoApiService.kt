package com.example.github.repositories.data.source

import com.example.github.repositories.data.RepositoryDTO
import com.example.github.repositories.data.Response
import com.example.github.repositories.data.UserDTO
import com.example.github.repositories.data.source.GitHubRepoApiService.Apis.search_repositories
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


private const val BASE_URL_DEV = "https://api.github.com/"

private const val url = BASE_URL_DEV
private val retrofit = Retrofit.Builder()
    .baseUrl(url)
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .build()
object GitHubRepoApi {
    val retrofitService: GitHubRepoApiService by lazy {
        retrofit.create(GitHubRepoApiService::class.java)
    }
}

interface GitHubRepoApiService {
    object Apis {
        const val search_repositories = "search/repositories"
    }


    @GET(search_repositories)
    suspend fun searchRepositories(
        @Query("q") q: String?,
        @Query("sort") sort: String?,
        @Query("order") order: String?
    ): Response?


    @GET("users/{username}")
    suspend fun getUser(
        @Path("username") username: String?
    ): UserDTO?


    @GET
    suspend fun getUserRepositories(
        @Url userRepo: String?
    ): MutableList<RepositoryDTO>?

}