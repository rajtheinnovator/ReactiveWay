package com.enpassio.reactiveway.instantsearch.network

import com.enpassio.reactiveway.instantsearch.network.model.Contact
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    @GET("contacts.php")
    fun getContacts(@Query("source") source: String, @Query("search") query: String): Single<List<Contact>>
}