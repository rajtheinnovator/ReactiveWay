package com.enpassio.reactiveway

import android.text.TextUtils
import android.util.Log
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


object ServiceGenerator {

    val API_BASE_URL = "https://github.com/login/oauth/"
    val httpClient = OkHttpClient.Builder()
    private val builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())

    fun <S> createService(serviceClass: Class<S>): S {
        return createService(serviceClass, null)
    }

    fun <S> createService(
            serviceClass: Class<S>, clientId: String?, clientSecret: String?): S {
        if (!TextUtils.isEmpty(clientId) && !TextUtils.isEmpty(clientSecret)) {
            val credentials = Credentials.basic(clientId!!, clientSecret!!)
            return createService(serviceClass, credentials)
        }

        return createService(serviceClass, clientId, clientSecret)
    }

    fun <S> createService(
            serviceClass: Class<S>, credentials: String?): S {
        if (!TextUtils.isEmpty(credentials)) {

            httpClient.addInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request = chain.request()
                            .newBuilder()
                            .header("Authorization", credentials!!)
                            .header("Accept", "application/json")
                            .build()
                    Log.v("my_tag", "url is: " + request.url().toString())
                    return chain.proceed(request)
                }
            })
        }
        return builder.client(httpClient.build()).build().create(serviceClass)
    }
}