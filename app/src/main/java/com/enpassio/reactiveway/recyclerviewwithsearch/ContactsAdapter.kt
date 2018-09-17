package com.enpassio.reactiveway.recyclerviewwithsearch

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.enpassio.reactiveway.R


class ContactsAdapter(private val context: Context,
                      private val contactList: List<Contact>,
                      private val listener: ContactsAdapterListener)
    : RecyclerView.Adapter<ContactsAdapter.MyViewHolder>(), Filterable {

    private var contactListFiltered: ArrayList<Contact>? = null

    // name match condition. this might differ depending on your requirement
    // here we are looking for name or phone number match

    override fun getFilter(): Filter {
        val filter = object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    val arrayListOfContacts = ArrayList<Contact>()
                    for (item in contactList) {
                        arrayListOfContacts.add(item)
                    }
                    contactListFiltered = arrayListOfContacts
                } else {
                    val filteredList = ArrayList<Contact>()
                    for (row in contactList) {
                        if (row.name!!.toLowerCase().contains(charString.toLowerCase()) || row.phone!!.contains(charSequence)) {
                            filteredList.add(row)
                        }
                    }

                    contactListFiltered = filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = contactListFiltered

                for (item in contactListFiltered!!) {
                    Log.v("my_tag", "item is: " + item)
                }

                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                contactListFiltered = filterResults.values as ArrayList<Contact>
                notifyDataSetChanged()
            }
        }
        return filter
    }


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView
        var phone: TextView
        var thumbnail: ImageView

        init {
            name = view.findViewById(R.id.name)
            phone = view.findViewById(R.id.phone)
            thumbnail = view.findViewById(R.id.thumbnail)

            view.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    // send selected contact in callback
                    listener.onContactSelected(contactListFiltered!![adapterPosition])
                }
            })
        }
    }


    init {
        val arrayListOfContacts = ArrayList<Contact>()
        for (item in contactList) {
            arrayListOfContacts.add(item)
        }
        this.contactListFiltered = arrayListOfContacts
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.user_row_item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val contact = contactListFiltered!![position]
        holder.name.text = contact.name
        holder.phone.text = contact.phone

        Glide.with(context)
                .load(contact.image)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.thumbnail)
    }

    override fun getItemCount(): Int {
        return contactListFiltered!!.size
    }

    interface ContactsAdapterListener {
        fun onContactSelected(contact: Contact)
    }
}