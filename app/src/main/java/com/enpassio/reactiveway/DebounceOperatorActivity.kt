package com.enpassio.reactiveway

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import butterknife.ButterKnife
import butterknife.Unbinder
import com.jakewharton.rxbinding2.widget.RxTextView
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_debounce_operator.*
import java.util.concurrent.TimeUnit


class DebounceOperatorActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()
    private var unbinder: Unbinder? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debounce_operator)
        unbinder = ButterKnife.bind(this)

        disposable.add(
                RxTextView.textChangeEvents(input_search)
                        .skipInitialValue()
                        .debounce(300, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(searchQuery()))

        txt_search_string!!.text = "Search query will be accumulated every 300 milli sec"
    }

    private fun searchQuery(): DisposableObserver<TextViewTextChangeEvent> {
        return object : DisposableObserver<TextViewTextChangeEvent>() {
            override fun onNext(textViewTextChangeEvent: TextViewTextChangeEvent) {
                Log.d(TAG, "DebounceOperator search string: " + textViewTextChangeEvent.text().toString())

                txt_search_string!!.text = "Query: " + textViewTextChangeEvent.text().toString()
            }

            override fun onError(e: Throwable) {

            }

            override fun onComplete() {

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbinder!!.unbind()
        disposable.clear()
    }

    companion object {

        private val TAG = DebounceOperatorActivity::class.java.simpleName
    }
}