package com.enpassio.reactiveway

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Predicate
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers


class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    private var compositeDisposable = CompositeDisposable()

    private fun getObservable(): Observable<String> {
        return Observable.fromArray("Item 1",
                "Item 2",
                "Item 3",
                "Item 4",
                "Wow 5",
                "Amazing 6")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val buttonCLick: Button = findViewById(R.id.authenticate)
        buttonCLick.setOnClickListener({ view -> addObserver() })
    }
    private fun addObserver() {
        val myObserver = getMyObserver()
        val myCapsObserver = getMyCapsObserver()

        val myObservable = getObservable()
        compositeDisposable.add(myObservable
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
                .subscribeWith(myObserver))


        compositeDisposable.add(myObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(object : Predicate<String> {
                    @Throws(Exception::class)
                    override fun test(s: String): Boolean {
                        return s.toLowerCase().startsWith("a")
                    }
                })
                .map { s: String -> s.toUpperCase() }
                .subscribeWith(myCapsObserver))


        //.map { s:String -> s.toUpperCase() }
    }

    private fun getMyCapsObserver(): DisposableObserver<String> {
        return object : DisposableObserver<String>() {
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

    private fun getMyObserver(): DisposableObserver<String> {
        return object : DisposableObserver<String>() {
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
        no need to listen to **any** event when the activity is destroyed,
        so dispose the observer
        */
        compositeDisposable.clear()
    }
}