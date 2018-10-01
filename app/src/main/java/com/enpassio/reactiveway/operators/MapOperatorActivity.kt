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


class MapOperatorActivity : AppCompatActivity() {

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.operators_activity_main)

        /*
        create a map and add to the observer so that it returns items emitted by the observables
        but after doing the map operation
        */
        usersObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { User -> User((String.format("%s@emailserviceprovider.com", User.name)), User.name?.toUpperCase()) }
                .subscribe(object : Observer<User> {
                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                    }

                    override fun onNext(user: User) {
                        Log.d(TAG, "MapOperatoronNext: " + user.name + ", " + user.gender)
                    }

                    override fun onError(e: Throwable) {
                        Log.d(TAG, "MapOperator onError: " + e.message)
                    }

                    override fun onComplete() {
                        Log.d(TAG, "MapOperator, All users emitted!")
                    }
                })
    }

    /**
     * Assume this method is making a network call and fetching Users
     * an Observable that emits list of users
     * each User has name and gender, but missing email id
     */
    private val usersObservable: Observable<User>
        get() {
            val names = arrayOf("user1", "user2", "user3", "user4", "user5")

            val users = ArrayList<User>()
            for (name in names) {
                val user = User()
                user.name = (name)
                user.gender = ("male")

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


    override fun onDestroy() {
        super.onDestroy()
        disposable!!.dispose()
    }

    companion object {

        private val TAG = MapOperatorActivity::class.java.simpleName
    }
}