package com.enpassio.reactiveway

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import hu.akarnokd.rxjava2.math.MathObservable
import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MathematicalOperatorExample : AppCompatActivity() {

    companion object {

        private val TAG = MathematicalOperatorExample::class.java.simpleName
    }

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        operatorMaxExample()
        operatorMinExample()
        operatorSumExample()
        operatorAverageExample()
        operatorCountExample()
        operatorReduceExample()

    }

    private fun operatorReduceExample() {
        Observable
                .range(1, 10)
                .reduce { sum, int -> sum + int }
                .subscribe(object : MaybeObserver<Int> {
                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                    }

                    override fun onSuccess(integer: Int) {
                        Log.d(TAG, "Mathematical operator  REDUCE, Sum of numbers from 1 to 10 is: " + integer)
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "onError: " + e.message)
                    }

                    override fun onComplete() {
                        Log.e(TAG, "onComplete")
                    }
                })
    }

    private fun operatorCountExample() {
        getUsersObservable()
                .filter { User -> (User.gender!!.equals("male", ignoreCase = true)) }
                .count()
                .subscribeWith(object : SingleObserver<Long> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(count: Long) {
                        Log.d(TAG, "Mathematical operator  COUNT, Male users count: " + count)
                    }

                    override fun onError(e: Throwable) {

                    }
                })
    }

    //for testing Count operator, we'll need the following method
    private fun getUsersObservable(): Observable<User> {
        val maleUsers = arrayOf("user1", "user2", "user3")
        val femaleUsers = arrayOf("NewUser1", "NewUser2", "NewUser3", "NewUser4", "NewUser5")

        val users = ArrayList<User>()

        for (name in maleUsers) {
            val user = User()
            user.name = name
            user.gender = "male"

            users.add(user)
        }

        for (name in femaleUsers) {
            val user = User()
            user.name = name
            user.gender = "female"

            users.add(user)
        }
        return Observable
                .create(ObservableOnSubscribe<User> { emitter ->
                    for (user in users) {
                        if (!emitter.isDisposed) {
                            emitter.onNext(user)
                        }
                    }

                    if (!emitter.isDisposed) {
                        emitter.onComplete()
                    }
                }).subscribeOn(Schedulers.io())
    }

    private fun operatorAverageExample() {
        val observable = Observable.fromArray(5, 101, 404, 22, 3, 1024, 65)
        MathObservable
                .averageDouble(observable)
                .subscribe(object : Observer<Double> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onComplete() {

                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onNext(integer: Double) {
                        Log.d(TAG, "Mathematical operator AVERAGE, Average: " + integer.toInt())
                    }
                })
    }

    private fun operatorSumExample() {

        val observable = Observable.fromArray(5, 101, 404, 22, 3, 1024, 65)

        MathObservable
                .sumInt(observable)
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onComplete() {

                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onNext(integer: Int) {
                        Log.d(TAG, "Mathematical operator SUM, Summation value is: " + integer)
                    }
                })
    }

    private fun operatorMinExample() {

        val observable = Observable.fromArray(5, 101, 404, 22, 3, 1024, 65)

        MathObservable
                .min<Int>(observable)
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onComplete() {

                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onNext(integer: Int) {
                        Log.d(TAG, "Mathematical operator MIN, Min value: " + integer)
                    }
                })
    }

    private fun operatorMaxExample() {

        val observable = Observable.fromArray(5, 101, 404, 22, 3, 1024, 65)
        MathObservable
                .max(observable)
                .subscribe(object : Observer<Int> {
                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onNext(integer: Int) {
                        Log.d(TAG, "Mathematical operator MAX, Max value: " + integer)
                    }
                })
    }
}

