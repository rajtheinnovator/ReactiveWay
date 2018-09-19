package com.enpassio.reactiveway.flightapp.network.model

import com.google.gson.annotations.SerializedName


class Price {
    var price: Float = 0.toFloat()
        internal set
    var seats: String? = null
        internal set
    var currency: String? = null
        internal set

    @SerializedName("flight_number")
    var flightNumber: String? = null
        internal set

    var from: String? = null
        internal set
    var to: String? = null
        internal set
}