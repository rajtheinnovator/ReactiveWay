package com.enpassio.reactiveway.githubexample

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable


class GitHubClient private constructor() {
    private val gitHubService: GitHubService

    init {
        val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
        val retrofit = Retrofit.Builder().baseUrl(GITHUB_BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        gitHubService = retrofit.create(GitHubService::class.java)
    }

    fun getStarredRepos(userName: String): Observable<List<GitHubRepo>> {
        return gitHubService.getStarredRepositories(userName)
    }

    companion object {

        private val GITHUB_BASE_URL = "https://api.github.com/"

        private var instance: GitHubClient? = null

        fun getInstance(): GitHubClient {
            if (instance == null) {
                instance = GitHubClient()
            }
            return instance!!
        }
    }
}