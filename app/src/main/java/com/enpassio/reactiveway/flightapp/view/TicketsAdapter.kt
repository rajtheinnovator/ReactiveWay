package com.enpassio.reactiveway.flightapp.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.enpassio.reactiveway.R
import com.enpassio.reactiveway.flightapp.network.model.Ticket
import com.github.ybq.android.spinkit.SpinKitView


class TicketsAdapter(private val context: Context,
                     private val contactList: ArrayList<Ticket>,
                     private val listener: TicketsAdapterListener)
    : RecyclerView.Adapter<TicketsAdapter.MyViewHolder>() {
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var airlineName: TextView

        var logo: ImageView

        var stops: TextView

        var seats: TextView

        var departure: TextView

        var arrival: TextView

        var duration: TextView

        var price: TextView

        var loader: SpinKitView

        init {
            airlineName = view.findViewById(R.id.airline_name)
            logo = view.findViewById(R.id.logo)
            stops = view.findViewById(R.id.number_of_stops)
            seats = view.findViewById(R.id.number_of_seats)
            departure = view.findViewById(R.id.departure)
            arrival = view.findViewById(R.id.arrival)
            duration = view.findViewById(R.id.duration)
            price = view.findViewById(R.id.price)
            loader = view.findViewById(R.id.loader)


            view.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    // send selected contact in callback
                    listener.onTicketSelected(contactList[adapterPosition])
                }
            })
        }

        fun bindTicketsList(ticketsList: ArrayList<Ticket>, context: Context, position: Int) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.ticket_row, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.bindTicketsList(contactList, context, position)
        val ticket = contactList[position]

        Glide.with(context)
                .load(ticket.airline!!.logo)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.logo)

        holder.airlineName.setText(ticket.airline!!.name)

        holder.departure.setText(ticket.departure + " Dep")
        holder.arrival.setText(ticket.arrival + " Dest")

        holder.duration.setText(ticket.flightNumber)
        holder.duration.append(", " + ticket.duration)
        holder.stops.setText(ticket.numberOfStops.toString() + " Stops")

        if (!TextUtils.isEmpty(ticket.instructions)) {
            holder.duration.append(", " + ticket.instructions)
        }

        if (ticket.price != null) {
            holder.price.text = "₹" + String.format("%.0f", ticket.price!!.price)
            holder.seats.setText(ticket!!.price!!.seats + " Seats")
            holder.loader.visibility = View.INVISIBLE
        } else {
            holder.loader.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    interface TicketsAdapterListener {
        fun onTicketSelected(contact: Ticket)
    }
}