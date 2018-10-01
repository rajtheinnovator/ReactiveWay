package com.enpassio.reactiveway.operators

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.enpassio.reactiveway.R
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.CompletableObserver
import io.reactivex.CompletableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class CompletableObservableCompletableObserverActivity : AppCompatActivity() {
    private var disposable: Disposable? = null

    /**
     * Completable won't emit any item, instead it returns
     * Success or failure state
     * Consider an example of making a PUT request to server to update
     * something where you are not expecting any response but the
     * success status
     * -
     * Completable : CompletableObserver
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myModel = MyModel(1, "A new MyModel object it is!")

        val completableObservable = updateMyModel(myModel)

        val completableObserver = completableObserver()

        completableObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(completableObserver)
    }


    /**
     * Assume this making PUT request to server to update the myModel object
     */
    private fun updateMyModel(myModel: MyModel): Completable {
        return Completable.create(object : CompletableOnSubscribe {
            @Throws(Exception::class)
            override fun subscribe(emitter: CompletableEmitter) {
                if (!emitter.isDisposed) {
                    Thread.sleep(1000)
                    emitter.onComplete()
                }
            }
        })
    }

    private fun completableObserver(): CompletableObserver {
        return object : CompletableObserver {
            override fun onSubscribe(d: Disposable) {
                Log.d(TAG, "Completable onSubscribe")
                disposable = d
            }

            override fun onComplete() {
                Log.d(TAG, "Completable onComplete: MyModel object updated successfully!")
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "Completable onError: " + e.message)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable!!.dispose()
    }

    companion object {

        private val TAG = CompletableObservableCompletableObserverActivity::class.java.simpleName
    }
}