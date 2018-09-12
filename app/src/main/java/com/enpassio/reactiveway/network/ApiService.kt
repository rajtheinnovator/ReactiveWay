package com.enpassio.reactiveway.network

import com.enpassio.reactiveway.network.model.Note
import com.enpassio.reactiveway.network.model.User
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*


interface ApiService {
    // Register new user
    @FormUrlEncoded
    @POST("notes/user/register")
    fun register(@Field("device_id") deviceId: String): Single<User>

    // Create note
    @FormUrlEncoded
    @POST("notes/new")
    fun createNote(@Field("note") note: String): Single<Note>

    // Fetch all notes
    @GET("notes/all")
    fun fetchAllNotes(): Single<List<Note>>

    // Update single note
    @FormUrlEncoded
    @PUT("notes/{id}")
    fun updateNote(@Path("id") noteId: Int, @Field("note") note: String): Completable

    // Delete note
    @DELETE("notes/{id}")
    fun deleteNote(@Path("id") noteId: Int): Completable
}