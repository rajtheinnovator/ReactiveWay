package com.enpassio.reactiveway.operators

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.enpassio.reactiveway.R
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class SingleObservableSingleObserverActivity : AppCompatActivity() {

    private var disposable: Disposable? = null

    /**
     * Single Observable emitting single MyModel object
     * Single Observable is more useful in making network calls
     * where you expect a single response object to be emitted
     * -
     * Single : SingleObserver
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.operators_activity_main)

        val myModelObservable = getMyModelObservable

        val singleObserver = getSingleObserver

        myModelObservable
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(singleObserver)

    }

    private val getSingleObserver: SingleObserver<MyModel>
        get() = object : SingleObserver<MyModel> {
            override fun onSubscribe(d: Disposable) {
                Log.d(TAG, "SingleObservableSingleObserver onSubscribe")
                disposable = d
            }

            override fun onSuccess(myModel: MyModel) {
                Log.d(TAG, "SingleObservableSingleObserver onSuccess: " + myModel.myData)
            }

            override fun onError(e: Throwable) {
                Log.d(TAG, "SingleObservableSingleObserver onError: " + e.message)
            }
        }

    private val getMyModelObservable: Single<MyModel>
        get() = Single.create(SingleOnSubscribe<MyModel> { emitter ->
            val myModel = MyModel(1, "This is my data")
            emitter.onSuccess(myModel)
        })


    override fun onDestroy() {
        super.onDestroy()
        disposable!!.dispose()
    }

    companion object {

        private val TAG = SingleObservableSingleObserverActivity::class.java.simpleName
    }
}