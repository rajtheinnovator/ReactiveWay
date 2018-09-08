package com.enpassio.reactiveway

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*


class FlatMapActivity : AppCompatActivity() {

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usersObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { User -> getAddressObservable(User) }
                .subscribe(object : Observer<User> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "FlatMap onSubscribe")
                        disposable = d
                    }

                    override fun onNext(user: User) {
                        Log.d(TAG, "FlatMap onNext: " + user.name + ", " + user.gender + ", " + user.address)
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {
                        Log.d(TAG, "FlatMap All users emitted!")
                    }
                })
    }

    /**
     * Assume this is a network call to fetch users
     * but it returns Users with name and gender while it is missing the address
     */
    private val usersObservable: Observable<User>
        get() {
            val maleUsers = arrayOf("user1", "user2", "user3", "user4", "user5")

            val users = ArrayList<User>()

            for (name in maleUsers) {
                val user = User()
                user.name = name
                user.gender = "male"

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


    /**
     * Assume this as a network call
     * returns Users with address field added
     */
    private fun getAddressObservable(user: User): Observable<User> {
        val addresses = arrayOf("Address value 1", "This is address 2", "Address 3 is correct", "Address 4 is also fine", "users address 5 is also fine")

        return Observable
                .create(ObservableOnSubscribe<User> { emitter ->
                    val address = Address()
                    address.address = addresses[Random().nextInt(2) + 0]
                    if (!emitter.isDisposed) {
                        user.address = address


                        // Generate network latency of random duration
                        val sleepTime = Random().nextInt(1000) + 500

                        Thread.sleep(sleepTime.toLong())
                        emitter.onNext(user)
                        emitter.onComplete()
                    }
                }).subscribeOn(Schedulers.io())
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable!!.dispose()
    }

    companion object {

        private val TAG = FlatMapActivity::class.java.simpleName
    }
}