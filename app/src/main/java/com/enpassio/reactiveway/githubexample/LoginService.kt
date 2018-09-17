package com.enpassio.reactiveway.githubexample


import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query


interface LoginService {
    @FormUrlEncoded
    @POST("/login/oauth/access_token")
    fun getAccessToken(
            @Field("code") code: String,
            @Field("client_id") clientId: String,
            @Field("client_secret") clientSecret: String): Call<AccessToken>
}

interface UsersService {
    @POST("/user")
    fun getUsersData(
            @Query("scope") scope: String,
            @Query("access_token") token: String): Call<User>
}