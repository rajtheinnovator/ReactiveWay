package com.enpassio.reactiveway.operators

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.enpassio.reactiveway.R
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class ObservablesAndObserversActivity : AppCompatActivity() {

    private var disposable: Disposable? = null

    /**
     * Simple Observable emitting multiple MyModel objects
     * -
     * Observable : Observer
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myModelObservable = myModelObservable

        val myModelObserver = myModelObserver

        myModelObservable.observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribeWith(myModelObserver)
    }

    private val myModelObserver: Observer<MyModel>
        get() = object : Observer<MyModel> {

            override fun onSubscribe(d: Disposable) {
                Log.d(TAG, "Observer<MyModel> onSubscribe")
                disposable = d
            }

            override fun onNext(myModelObject: MyModel) {
                Log.d(TAG, "Observer<MyModel> onNext: " + myModelObject.myData)
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "Observer<MyModel> onError: " + e.message)
            }

            // all data are emitted
            override fun onComplete() {
                Log.d(TAG, "Observer<MyModel> onComplete")
            }
        }

    private val myModelObservable: Observable<MyModel>
        get() {
            val myModelDataList = prepareMyModelDatas()

            return Observable.create(ObservableOnSubscribe<MyModel> { emitter ->
                for (myModelObject in myModelDataList) {
                    if (!emitter.isDisposed) {
                        emitter.onNext(myModelObject)
                    }
                }
                if (!emitter.isDisposed) {
                    emitter.onComplete()
                }
            })
        }


    private fun prepareMyModelDatas(): List<MyModel> {
        val myModelList = ArrayList<MyModel>()
        myModelList.add(MyModel(1, "This is my model data 1!"))
        myModelList.add(MyModel(2, "2 is a nice number for my model data!"))
        myModelList.add(MyModel(3, "data is good when you have 3 sets of it"))
        myModelList.add(MyModel(4, "my model data used to get up at 4 :P"))
        return myModelList
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable!!.dispose()
    }

    companion object {

        private val TAG = ObservablesAndObserversActivity::class.java.simpleName
    }
}