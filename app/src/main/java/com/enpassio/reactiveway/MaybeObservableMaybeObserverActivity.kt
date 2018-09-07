package com.enpassio.reactiveway

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.Maybe
import io.reactivex.MaybeObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class MaybeObservableMaybeObserverActivity : AppCompatActivity() {

    private var disposable: Disposable? = null

    /**
     * Consider an example getting a myModel object from database using ID
     * There is possibility of not finding the myModel by ID in the database
     * In this situation, MayBe can be used
     * -
     * Maybe : MaybeObserver
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myModelObservable = getMyModelObservable

        val myModelObserver = getMyModelObserver

        myModelObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(myModelObserver)
    }

    private val getMyModelObserver: MaybeObserver<MyModel>
        get() = object : MaybeObserver<MyModel> {

            override fun onSubscribe(d: Disposable) {
                disposable = d
            }

            override fun onSuccess(myModel: MyModel) {
                Log.d(TAG, "onSuccess: " + myModel.myData)
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "onError: " + e.message)
            }

            override fun onComplete() {
                Log.e(TAG, "onComplete")
            }
        }

    /**
     * Emits optional data (0 or 1 emission)
     * But for now it emits 1 myModel object always
     */
    private val getMyModelObservable: Maybe<MyModel>
        get() = Maybe.create { emitter ->
            val myModel = MyModel(1, "This is myModel object!")
            if (!emitter.isDisposed) {
                emitter.onSuccess(myModel)
            }
        }


    override fun onDestroy() {
        super.onDestroy()
        disposable!!.dispose()
    }

    companion object {

        private val TAG = MaybeObservableMaybeObserverActivity::class.java.simpleName
    }
}