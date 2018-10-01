package com.enpassio.reactiveway.instantsearch.view

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.*
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import com.enpassio.reactiveway.R
import com.enpassio.reactiveway.instantsearch.adapter.ContactsAdapter
import com.enpassio.reactiveway.instantsearch.network.ApiClient
import com.enpassio.reactiveway.instantsearch.network.ApiService
import com.enpassio.reactiveway.instantsearch.network.model.Contact
import com.jakewharton.rxbinding2.widget.RxTextView
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit


class RemoteSearchActivity : AppCompatActivity(), ContactsAdapter.ContactsAdapterListener {

    private val disposable = CompositeDisposable()
    private val publishSubject: PublishSubject<String> = PublishSubject.create()
    private var apiService: ApiService? = null
    private var mAdapter: ContactsAdapter? = null
    private val contactsList = ArrayList<Contact>()


    lateinit var inputSearch: EditText

    lateinit var recyclerView: RecyclerView


    private val searchObserver: DisposableObserver<List<Contact>>
        get() = object : DisposableObserver<List<Contact>>() {
            override fun onNext(contacts: List<Contact>) {
                contactsList.clear()
                contactsList.addAll(contacts)
                mAdapter!!.notifyDataSetChanged()
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "onError: " + e.message)
            }

            override fun onComplete() {

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.instant_search_activity_remote_search)

        recyclerView = findViewById(R.id.recycler_view)
        inputSearch = findViewById(R.id.input_search)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mAdapter = ContactsAdapter(this, contactsList, this)

        val mLayoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = mAdapter

        whiteNotificationBar(recyclerView)

        apiService = ApiClient.client.create(ApiService::class.java)

        val observer = searchObserver

        disposable.add(publishSubject.debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .switchMapSingle { query ->
                    apiService!!.getContacts("", query)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                }
                /*
                .switchMapSingle(object : Function<String, Single<List<Contact>>>() {
                    @Throws(Exception::class)
                    fun apply(s: String): Single<List<Contact>> {
                        return apiService!!.getContacts(null, s)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                    }
                })
                */
                .subscribeWith(observer))


        // skipInitialValue() - skip for the first time when EditText empty
        disposable.add(RxTextView.textChangeEvents(inputSearch)
                .skipInitialValue()
                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(searchContactsTextWatcher()))

        disposable.add(observer)

        // passing empty string fetches all the contacts
        publishSubject.onNext("")
    }

    private fun searchContactsTextWatcher(): DisposableObserver<TextViewTextChangeEvent> {
        return object : DisposableObserver<TextViewTextChangeEvent>() {
            override fun onNext(textViewTextChangeEvent: TextViewTextChangeEvent) {
                Log.d(TAG, "Search query: " + textViewTextChangeEvent.text())
                publishSubject.onNext(textViewTextChangeEvent.text().toString())
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "onError: " + e.message)
            }

            override fun onComplete() {

            }
        }
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

    override fun onContactSelected(contact: Contact) {

    }

    private fun whiteNotificationBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.getSystemUiVisibility()
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.setSystemUiVisibility(flags)
            window.statusBarColor = Color.WHITE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId().equals(android.R.id.home)) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {

        private val TAG = RemoteSearchActivity::class.java.simpleName
    }
}