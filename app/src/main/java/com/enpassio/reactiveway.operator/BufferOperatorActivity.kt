package com.enpassio.reactiveway.operator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.enpassio.reactiveway.R
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.operator_activity_buffer.*
import java.util.concurrent.TimeUnit


class BufferOperatorActivity : AppCompatActivity() {

    @BindView(R.id.tap_result)
    internal var txtTapResult: TextView? = null

    @BindView(R.id.tap_result_max_count)
    internal var txtTapResultMax: TextView? = null

    private var disposable: Disposable? = null
    private var unbinder: Unbinder? = null
    private var maxTaps = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.operator_activity_buffer)

        simpleExampleOfBuffer()

        unbinder = ButterKnife.bind(this)

        RxView.clicks(layout_tap_area)
                .map { Any -> 1 }
                .buffer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : Observer<List<Int>> {
                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                    }

                    override fun onNext(integers: List<Int>) {
                        Log.d(TAG, "BufferOperator onNext: " + integers.size + " taps received!")
                        if (integers.size > 0) {
                            maxTaps = if (integers.size > maxTaps) integers.size else maxTaps
                            txtTapResult!!.text = String.format("Received %d taps in 3 secs", integers.size)
                            txtTapResultMax!!.text = String.format("Maximum of %d taps received in this session", maxTaps)
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.d(TAG, "BufferOperator onError: " + e.message)
                    }

                    override fun onComplete() {
                        Log.d(TAG, "BufferOperator onComplete")
                    }
                })
    }

    private fun simpleExampleOfBuffer() {
        val integerObservable = Observable.just(1, 2, 3, 4,
                5, 6, 7, 8, 9)

        integerObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .buffer(3)
                .subscribe(object : Observer<List<Int>> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(integers: List<Int>) {
                        Log.d("inside_simple_example", "onNext")
                        for (integer in integers) {
                            Log.d("inside_simple_example", "Item: $integer")
                        }
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {
                        Log.d("inside_simple_example", "All items emitted!")
                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        unbinder!!.unbind()
        disposable!!.dispose()
    }

    companion object {
        private val TAG = "my_tag"
    }
}