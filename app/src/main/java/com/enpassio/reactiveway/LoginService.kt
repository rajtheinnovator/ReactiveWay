package com.enpassio.reactiveway


import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface LoginService {
    @FormUrlEncoded
    @POST("/login/oauth/access_token")
    fun getAccessToken(
            @Field("code") code: String,
            @Field("client_id") clientId: String,
            @Field("client_secret") clientSecret: String): Call<AccessToken>
}

interface UsersService {
    @FormUrlEncoded
    @POST("/user")
    fun getUsersData(
            @Field("access_token") token: String
    ): Call<User>
}