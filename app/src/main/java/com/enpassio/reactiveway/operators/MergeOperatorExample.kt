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


class MergeOperatorExample : AppCompatActivity() {

    companion object {

        private val TAG = MergeOperatorExample::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.operators_activity_main)
        seeMergeExample()
    }

    private fun seeMergeExample() {
        Observable
                .merge(getMaleObservable(), getFemaleObservable())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : Observer<User> {
                    override fun onSubscribe(d: Disposable) {}

                    override fun onNext(user: User) {
                        Log.d(TAG, user.name + ", " + user.gender)
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {

                    }
                })
    }

    private fun getFemaleObservable(): Observable<User> {
        val names = arrayOf("User1", "User2", "User3")

        val users = ArrayList<User>()
        for (name in names) {
            val user = User()
            user.name = name
            user.gender = "female"

            users.add(user)
        }
        return Observable
                .create(ObservableOnSubscribe<User> { emitter ->
                    for (user in users) {
                        if (!emitter.isDisposed) {
                            Thread.sleep(1000)
                            emitter.onNext(user)
                        }
                    }

                    if (!emitter.isDisposed) {
                        emitter.onComplete()
                    }
                }).subscribeOn(Schedulers.io())
    }

    private fun getMaleObservable(): Observable<User> {
        val names = arrayOf("NewUser1", "NewUser2", "NewUser3", "NewUser4", "NewUser5")

        val users = ArrayList<User>()

        for (name in names) {
            val user = User()
            user.name = name
            user.gender = "male"

            users.add(user)
        }
        return Observable
                .create(ObservableOnSubscribe<User> { emitter ->
                    for (user in users) {
                        if (!emitter.isDisposed) {
                            Thread.sleep(500)
                            emitter.onNext(user)
                        }
                    }

                    if (!emitter.isDisposed) {
                        emitter.onComplete()
                    }
                }).subscribeOn(Schedulers.io())
    }
}