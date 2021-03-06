package com.enpassio.reactiveway.githubexample

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UsersClient {
    private var retrofit: Retrofit? = null

    val client: Retrofit
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            retrofit = Retrofit.Builder()
                    .baseUrl("http://api.github.com")
                    /*
                    explicitly exclude the fields that we need only for database and not for actual news result
                    referenced from the link: @link:https://stackoverflow.com/a/41336692/5770629
                    */
                    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()))
                    .client(client)
                    .build()

            return retrofit!!
        }
}