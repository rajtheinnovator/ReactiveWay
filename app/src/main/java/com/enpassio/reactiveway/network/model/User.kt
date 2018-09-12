package com.enpassio.reactiveway.network.model

import com.google.gson.annotations.SerializedName


class User(
        @SerializedName("api_key")
        var spiKey: String)

open class BaseResponses(var error: String? = null)

class Note(var id: Int = 0,
           var note: String,
           var timestamp: String? = null) : BaseResponses()