package com.enpassio.reactiveway.githubexample

import retrofit2.http.GET
import retrofit2.http.Path
import rx.Observable


interface GitHubService {
    @GET("users/{user}/starred")
    fun getStarredRepositories(@Path("user") userName: String): Observable<List<GitHubRepo>>
}