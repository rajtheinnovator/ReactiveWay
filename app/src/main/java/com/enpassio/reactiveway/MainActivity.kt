package com.enpassio.reactiveway

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers


class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    private var disposable: Disposable? = null

    var myObservable = Observable
            .just("Item 1",
                    "Item 2",
                    "Item 3",
                    "Item 4")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val buttonCLick: Button = findViewById(R.id.authenticate)
        buttonCLick.setOnClickListener({ view -> addObserver() })
    }

    private fun addObserver() {
        val myObserver = getAnimalsObserver()
        myObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(object : Predicate<String> {
                    @Throws(Exception::class)
                    override fun test(s: String): Boolean {
                        return s.toLowerCase().endsWith("3")
                    }
                })
                /*
                subscribe vs subscribeWith explanation here
                at @Link: https://stackoverflow.com/a/44762520
                */
                .subscribeWith(myObserver);
    }


    private fun getAnimalsObserver(): Observer<String> {
        return object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                disposable = d
                Log.d(TAG, "onSubscribe")
            }


            override fun onNext(s: String) {
                Log.d(TAG, "onNext called with Name: $s")
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "onError called with error message: " + e.message)
            }

            override fun onComplete() {
                Log.d(TAG, "onComplete called and hence all items are emitted!")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        /*
        no need to listen to events when the activity is destroyed,
        so dispose the observer
        */
        disposable?.dispose()
    }
}