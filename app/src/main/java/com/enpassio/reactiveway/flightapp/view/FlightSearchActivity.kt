package com.enpassio.reactiveway.flightapp.view

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.enpassio.reactiveway.R
import com.enpassio.reactiveway.flightapp.network.ApiClient
import com.enpassio.reactiveway.flightapp.network.ApiService
import com.enpassio.reactiveway.flightapp.network.model.Ticket
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers


class FlightSearchActivity : AppCompatActivity(), TicketsAdapter.TicketsAdapterListener {

    private val disposable = CompositeDisposable()
    private var unbinder: Unbinder? = null

    private var apiService: ApiService? = null
    private var mAdapter: TicketsAdapter? = null
    private val ticketsList = ArrayList<Ticket>()
    private val listOfPositionOfTickets = ArrayList<String>()


    lateinit var recyclerView: RecyclerView

    lateinit var coordinatorLayout: CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flight_search)
        unbinder = ButterKnife.bind(this)

        recyclerView = findViewById(R.id.recycler_view)
        coordinatorLayout = findViewById(R.id.coordinator_layout)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle("$from > $to")

        apiService = ApiClient.client.create(ApiService::class.java)

        mAdapter = TicketsAdapter(this, ticketsList, this)

        val mLayoutManager = GridLayoutManager(this, 1)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.addItemDecoration(FlightSearchActivity.GridSpacingItemDecoration(1, dpToPx(5), true))
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = mAdapter

        val ticketsObservable = getTickets(from, to).replay()

        /**
         * Fetching all tickets first
         * Observable emits List<Ticket> at once
         * All the items will be added to RecyclerView
        </Ticket> */
        disposable.add(
                ticketsObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableObserver<List<Ticket>>() {

                            override fun onNext(tickets: List<Ticket>) {
                                // Refreshing list
                                ticketsList.clear()
                                ticketsList.addAll(tickets)
                                for (ticket in tickets) {
                                    listOfPositionOfTickets.add(ticket.flightNumber!!)
                                }

                                mAdapter!!.notifyDataSetChanged()
                            }

                            override fun onError(e: Throwable) {
                                showError(e)
                            }

                            override fun onComplete() {

                            }
                        }))

        /**
         * Fetching individual ticket price
         * First FlatMap converts single List<Ticket> to multiple emissions
         * Second FlatMap makes HTTP call on each Ticket emission
        </Ticket> */
        disposable.add(
                ticketsObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        /**
                         * Converting List<Ticket> emission to single Ticket emissions
                        </Ticket> */
                        .flatMap { tickets: List<Ticket> -> Observable.fromIterable(tickets) }
                        /**
                         * Fetching price on each Ticket emission
                         */
                        .flatMap { ticket: Ticket -> getPriceObservable(ticket) }
                        .subscribeWith(object : DisposableObserver<Ticket>() {
                            override fun onNext(ticket: Ticket) {
                                var position: Int? = 0
                                if (!ticketsList.isEmpty()) {
                                    position = listOfPositionOfTickets.indexOf(ticket.flightNumber)
                                }
                                if (position == -1) {
                                    // TODO - take action
                                    // Ticket not found in the list
                                    // This shouldn't happen
                                    return
                                }

                                ticketsList.set(position!!, ticket)
                                mAdapter!!.notifyItemChanged(position)
                            }

                            override fun onError(e: Throwable) {
                                showError(e)
                            }

                            override fun onComplete() {

                            }
                        }))

        // Calling connect to start emission
        ticketsObservable.connect()
    }

    /**
     * Making Retrofit call to fetch all tickets
     */
    private fun getTickets(from: String, to: String): Observable<List<Ticket>> {
        return apiService!!.searchTickets(from, to)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Making Retrofit call to get single ticket price
     * get price HTTP call returns Price object, but
     * map() operator is used to change the return type to Ticket
     */
    private fun getPriceObservable(ticket: Ticket): Observable<Ticket> {
        return apiService!!
                .getPrice(ticket.flightNumber!!, ticket.from!!, ticket.to!!)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { price -> Ticket(price = price) }
    }

    class GridSpacingItemDecoration(private val spanCount: Int, private val spacing: Int, private val includeEdge: Boolean) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view) // item position
            val column = position % spanCount // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing
                }
                outRect.bottom = spacing // item bottom
            } else {
                outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing // item top
                }
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        val r = resources
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r.displayMetrics))
    }

    override fun onTicketSelected(contact: Ticket) {

    }

    /**
     * Snackbar shows observer error
     */
    private fun showError(e: Throwable) {
        Log.e(TAG, "showError: " + e.message)

        val snackbar = Snackbar
                .make(coordinatorLayout!!, e.message.toString(), Snackbar.LENGTH_LONG)
        val sbView = snackbar.view
        val textView: TextView = sbView.findViewById(android.support.design.R.id.snackbar_text)
        textView.setTextColor(Color.YELLOW)
        snackbar.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
        unbinder!!.unbind()
    }

    companion object {

        private val TAG = FlightSearchActivity::class.java.simpleName
        private val from = "DEL"
        private val to = "HYD"
    }
}