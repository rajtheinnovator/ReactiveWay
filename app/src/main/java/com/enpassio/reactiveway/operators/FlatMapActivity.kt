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
import java.util.*


/* Order of execution is not maintained here for user's name as can be seen in the Logcat output example below */
/*
09-08 21:05:43.716 28078-28078/com.enpassio.reactiveway D/FlatMapActivity: FlatMap onSubscribe
09-08 21:05:44.477 28078-28150/com.enpassio.reactiveway D/FlatMapActivity: FlatMap onNext: user1, male, Address(address=This is address 2)
09-08 21:05:44.702 28078-28151/com.enpassio.reactiveway D/FlatMapActivity: FlatMap onNext: user2, male, Address(address=Address value 1)
09-08 21:05:44.824 28078-28153/com.enpassio.reactiveway D/FlatMapActivity: FlatMap onNext: user4, male, Address(address=Address value 1)
09-08 21:05:45.122 28078-28154/com.enpassio.reactiveway D/FlatMapActivity: FlatMap onNext: user5, male, Address(address=This is address 2)
09-08 21:05:45.210 28078-28152/com.enpassio.reactiveway D/FlatMapActivity: FlatMap onNext: user3, male, Address(address=This is address 2)
09-08 21:05:45.211 28078-28152/com.enpassio.reactiveway D/FlatMapActivity: FlatMap All users emitted!
*/
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