package com.enpassio.reactiveway

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
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
                .filter(object : Predicate<MyModel> {
                    @Throws(Exception::class)
                    override fun test(s: MyModel): Boolean {
                        return s.myData.toLowerCase().endsWith("e")
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
                .filter(object : Predicate<MyModel> {
                    @Throws(Exception::class)
                    override fun test(s: MyModel): Boolean {
                        return s.myData.toLowerCase().startsWith("d")
                    }
                })
                .map { s: MyModel -> MyModel(s.myId, s.myData.toUpperCase()) }
                .subscribeWith(myCapsObserver))
    }

    private fun getMyCapsObserver(): DisposableObserver<MyModel> {
        return object : DisposableObserver<MyModel>() {
            override fun onNext(s: MyModel) {
                Log.d(TAG, "onNext called with myData: ${s.myData}")
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "onError called with error message: " + e.message)
            }

            override fun onComplete() {
                Log.d(TAG, "onComplete called and hence all items are emitted!")
            }
        }
    }

    private fun getMyObserver(): DisposableObserver<MyModel> {
        return object : DisposableObserver<MyModel>() {
            override fun onNext(s: MyModel) {
                Log.d(TAG, "onNext called with myData: ${s.myData}")
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

    private fun getObservable(): Observable<MyModel> {
        val notes = getData()
        return Observable.create(ObservableOnSubscribe<MyModel> { emitter ->
            notes.forEach { note ->
                if (!emitter.isDisposed) {
                    emitter.onNext(note)
                }
            }

            if (!emitter.isDisposed) {
                emitter.onComplete()
            }
        })
    }

    private fun getData(): List<MyModel> {
        val myData = ArrayList<MyModel>()
        myData.add(MyModel(1, "data 1"))
        myData.add(MyModel(2, "data 2 is good"))
        myData.add(MyModel(3, "data 3 looks nice"))
        myData.add(MyModel(4, "data 4, you're amazing"))
        return myData
    }
}