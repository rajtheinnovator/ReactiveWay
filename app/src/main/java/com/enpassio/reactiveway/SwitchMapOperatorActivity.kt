package com.enpassio.reactiveway

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class SwitchMapOperatorActivity : AppCompatActivity() {

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val integerObservable = Observable.fromArray(arrayOf(1, 2, 3, 4, 5, 6))

        // it always emits 6 as it un-subscribes the previous observer
        integerObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .switchMap { Int -> getObservableSource(Int) }
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "onSubscribe")
                        disposable = d
                    }

                    override fun onNext(integer: Int) {
                        Log.d(TAG, "onNext: " + integer)
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {
                        Log.d(TAG, "All users emitted!")
                    }
                })
    }

    private fun getObservableSource(integer: Int): ObservableSource<Int> {
        return Observable.just(integer).delay(1, TimeUnit.SECONDS)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable!!.dispose()
    }

    companion object {

        private val TAG = SwitchMapOperatorActivity::class.java.simpleName
    }
}
