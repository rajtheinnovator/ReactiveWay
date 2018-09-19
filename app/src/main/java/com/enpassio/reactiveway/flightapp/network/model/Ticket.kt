package com.enpassio.reactiveway.flightapp.network.model

import com.google.gson.annotations.SerializedName


class Ticket(var from: String? = null,
             var to: String? = null,
             @SerializedName("flight_number")
             var flightNumber: String? = null,
             var departure: String? = null,
             var arrival: String? = null,
             var duration: String? = null,
             var instructions: String? = null,
             @SerializedName("stops")
             var numberOfStops: Int = 0,
             var airline: Airline? = null,
             var price: Price? = null) {

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }

        return if (other !is Ticket) {
            false
        } else flightNumber!!.equals(other.flightNumber!!, ignoreCase = true)

    }

    override fun hashCode(): Int {
        var hash = 3
        hash = 53 * hash + if (this.flightNumber != null) this.flightNumber!!.hashCode() else 0
        return hash
    }
}