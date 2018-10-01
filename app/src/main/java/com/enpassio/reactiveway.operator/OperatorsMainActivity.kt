package com.enpassio.reactiveway.operator

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.enpassio.reactiveway.MainActivity
import com.enpassio.reactiveway.R
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Predicate
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*


class OperatorsMainActivity : AppCompatActivity() {

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    private var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.operators_activity_main)

//        val buttonCLick: Button = findViewById(R.id.authenticate)
//        buttonCLick.setOnClickListener({ view -> addObserver() })
//
//        operatorFromArrayExample()
//        operatorRangeExample()
//        operatorChainingExample()
//        operatorJustWhichGivesJustOneEmissionExample()
//        operatorJustWhichGivesJustOneEmissionWithLoopsExample()
//        operatorFromWhichGivesNEmission()
//        operatorRepeatExample()
//
//        val observablesAndObserversActivityIntent = Intent(this, ObservablesAndObserversActivity::class.java)
//        startActivity(observablesAndObserversActivityIntent)
//
//        val singleObservableSingleObserverActivity = Intent(this, SingleObservableSingleObserverActivity::class.java)
//        startActivity(singleObservableSingleObserverActivity)
//
//        val maybeObservableMaybeObserverActivity = Intent(this, MaybeObservableMaybeObserverActivity::class.java)
//        startActivity(maybeObservableMaybeObserverActivity)
//
//        val completableObservableCompletableObserverActivity = Intent(this, CompletableObservableCompletableObserverActivity::class.java)
//        startActivity(completableObservableCompletableObserverActivity)
//
//        val flowableObservableSingleObserverActivity = Intent(this, FlowableObservableSingleObserverActivity::class.java)
//        startActivity(flowableObservableSingleObserverActivity)
//
//        val mapOperatorActivity = Intent(this, MapOperatorActivity::class.java)
//        startActivity(mapOperatorActivity)
//
//        val flatMapActivity = Intent(this, FlatMapActivity::class.java)
//        startActivity(flatMapActivity)
//
//        val concatMapOperatorActivity = Intent(this, ConcatMapOperatorActivity::class.java)
//        startActivity(concatMapOperatorActivity)
//
//        val switchMapOperatorActivity = Intent(this, SwitchMapOperatorActivity::class.java)
//        startActivity(switchMapOperatorActivity)
//
//        val bufferOperatorActivity = Intent(this, BufferOperatorActivity::class.java)
//        startActivity(bufferOperatorActivity)
//
//        val debounceOperatorActivity = Intent(this, DebounceOperatorActivity::class.java)
//        startActivity(debounceOperatorActivity)
//
//        val concatOperatorExample = Intent(this, ConcatOperatorExample::class.java)
//        startActivity(concatOperatorExample)
//
//        val mergeOperatorExample = Intent(this, MergeOperatorExample::class.java)
//        startActivity(mergeOperatorExample)
//
        val mathematicalOperatorExample = Intent(this, MathematicalOperatorExample::class.java)
        startActivity(mathematicalOperatorExample)
//
//        val mathematicalOperationOnCustomDataTypes = Intent(this, MathematicalOperationOnCustomDataTypes::class.java)
//        startActivity(mathematicalOperationOnCustomDataTypes)
    }

    private fun operatorRepeatExample() {
        Observable
                .range(1, 4)
                .repeat(3)
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "Subscribed")
                    }

                    override fun onNext(integer: Int) {
                        Log.d(TAG, "Inside Repeat, onNext: " + integer)
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {
                        Log.d(TAG, "Inside repeat, Completed emitting all numbers")
                    }
                })
    }

    private fun operatorFromWhichGivesNEmission() {
        val numbers = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)

        Observable.fromArray(*numbers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(integer: Int) {
                        Log.d(TAG, "Inside fromArray, onNext: " + integer)
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {
                        Log.d(TAG, "Inside fromArray, All numbers have been emitted!")
                    }
                })
    }

    private fun operatorJustWhichGivesJustOneEmissionWithLoopsExample() {
        val numbers = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
        val lotsOfNumbers = arrayOf(50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60)

        Observable.just(numbers, lotsOfNumbers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Array<Int>> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(integersArrayList: Array<Int>) {
                        for (number in integersArrayList) {
                            Log.d(TAG, "Inside just with loop, onNext: " + number)
                        }


                        // you might have to loop through the array
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {
                        Log.d(TAG, "Inside just with loops, All numbers have been emitted!")
                    }
                })
    }

    private fun operatorJustWhichGivesJustOneEmissionExample() {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(integer: Int) {
                        Log.d(TAG, "Inside just onNext: " + integer)
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {
                        Log.d(TAG, "Inside just All numbers have been emitted!")
                    }
                })
    }

    private fun operatorChainingExample() {
        Observable.range(1, 20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(object : Predicate<Int> {
                    @Throws(Exception::class)
                    override fun test(integer: Int): Boolean {
                        return integer % 2 == 0
                    }
                })
                .map { data: Int -> "Inside filter ${data} is even number" }
                .subscribe(object : Observer<String> {
                    override fun onSubscribe(d: Disposable) {}

                    override fun onNext(s: String) {
                        Log.d(TAG, "Inside filter onNext: $s")
                    }

                    override fun onError(e: Throwable) {}

                    override fun onComplete() {
                        Log.d(TAG, "Inside filter All numbers have been emitted!")
                    }
                })

    }

    private fun operatorRangeExample() {
        Observable.range(1, 20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableObserver<Int>() {
                    override fun onNext(integer: Int) {
                        Log.d(TAG, "Inside range, Number is: " + integer)
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {
                        Log.d(TAG, "Inside range, All have been numbers emitted!")
                    }
                })
    }

    private fun operatorFromArrayExample() {
        val numbers = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)

        Observable.fromArray(*numbers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableObserver<Int>() {
                    override fun onNext(integer: Int) {
                        Log.d(TAG, "Inside fromArray, Number is: " + integer)
                    }

                    override fun onError(e: Throwable) {}

                    override fun onComplete() {
                        Log.d(TAG, "Inside fromArray, All numbers have been emitted!")
                    }
                })
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