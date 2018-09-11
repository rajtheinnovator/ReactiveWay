package com.enpassio.reactiveway

import com.google.gson.annotations.SerializedName

class AccessToken(
        @SerializedName("access_token")
        var token: String? = null,
        @SerializedName("token_type")
        var tokenType: String? = null
)