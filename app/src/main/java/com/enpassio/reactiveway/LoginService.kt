package com.enpassio.reactiveway

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface LoginService {
    @FormUrlEncoded
    @POST("/login/oauth/access_token")
    fun getAccessToken(
            @Field("code") code: String): Call<AccessToken>
}