package com.enpassio.reactiveway.operator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.enpassio.reactiveway.R
import io.reactivex.Flowable
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers


class FlowableObservableSingleObserverActivity : AppCompatActivity() {

    private var disposable: Disposable? = null

    /**
     * Simple example of Flowable just to show the syntax
     * the use of Flowable is best explained when used with BackPressure
     * Read the below link to know the best use cases to use Flowable operator
     * https://github.com/ReactiveX/RxJava/wiki/What%27s-different-in-2.0#when-to-use-flowable
     * -
     * Flowable : SingleObserver
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val flowableObservable = getFlowableObservable

        val observer = getSingleObserver

        flowableObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .reduce(0, object : BiFunction<Int, Int, Int> {
                    override fun apply(result: Int, number: Int): Int {
                        Log.v(TAG, "Flowable Result: " + result + ", new number: " + number)
                        return result + number
                    }
                })
                .subscribe(observer)
    }

    private val getSingleObserver: SingleObserver<Int>
        get() = object : SingleObserver<Int> {
            override fun onSubscribe(d: Disposable) {
                Log.d(TAG, "Flowable onSubscribe")
                disposable = d
            }

            override fun onSuccess(integer: Int) {
                /*
                This is important to note, as it's the final thing we'd really see,
                all other values are just for seeing what's happening on background
                */
                Log.d(TAG, "Flowable onSuccess: " + integer)
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "Flowable onError: " + e.message)
            }
        }

    private val getFlowableObservable: Flowable<Int>
        get() = Flowable.range(1, 100)


    override fun onDestroy() {
        super.onDestroy()
        disposable!!.dispose()
    }

    companion object {

        private val TAG = FlowableObservableSingleObserverActivity::class.java.simpleName
    }
}