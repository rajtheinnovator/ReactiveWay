package com.enpassio.reactiveway.operators

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.enpassio.reactiveway.R
import hu.akarnokd.rxjava2.math.MathObservable
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers


class MathematicalOperatorExample : AppCompatActivity() {

    companion object {

        private val TAG = MathematicalOperatorExample::class.java.simpleName
    }

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*
        operatorMaxExample()
        operatorMinExample()
        operatorSumExample()
        operatorAverageExample()
        operatorCountExample()
        operatorReduceExample()
        operatorFilterExample()
        operatorFilterExampleOnCustomDataType()
        operatorSkipExample()
        operatorSkipLastExample()
        operatorTakeExample()
        operatorTakeLastExample()
        operatorDistinctExample()
        */
        operatorDistinctOnCustomDataTypeExample()


    }

    //[START] methods for DISTINCT operator on custom data type
    private fun operatorDistinctOnCustomDataTypeExample() {
        val notesObservable = getNotesObservable()

        val notesObserver = getNotesObserver()

        notesObservable.observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .distinct()
                .subscribeWith(notesObserver)
    }

    private fun getNotesObserver(): DisposableObserver<Note> {
        return object : DisposableObserver<Note>() {

            override fun onNext(note: Note) {
                Log.e(TAG, "Mathematical operator DISTINCT on custom data type, onNext: " + note.note!!)
            }

            override fun onError(e: Throwable) {

            }

            override fun onComplete() {
                Log.e(TAG, "onComplete")
            }
        }
    }

    private fun getNotesObservable(): Observable<Note> {
        val notes = prepareNotes()

        return Observable.create { emitter ->
            for (note in notes) {
                if (!emitter.isDisposed) {
                    emitter.onNext(note)
                }
            }

            if (!emitter.isDisposed) {
                emitter.onComplete()
            }
        }
    }

    // Preparing notes including duplicates
    private fun prepareNotes(): List<Note> {
        val notes = ArrayList<Note>()
        notes.add(Note(1, "Buy tooth paste!"))
        notes.add(Note(2, "Call brother!"))
        notes.add(Note(3, "Call brother!"))
        notes.add(Note(4, "Pay power bill!"))
        notes.add(Note(5, "Watch Narcos tonight!"))
        notes.add(Note(6, "Buy tooth paste!"))
        notes.add(Note(7, "Pay power bill!"))

        return notes
    }
    //[END] methods for DISTINCT operator on custom data type

    //[START] methods for DISTINCT operator on primitive data type
    private fun operatorDistinctExample() {
        val numbersObservable = Observable.just(10, 10, 15, 20, 100, 200, 100, 300, 20, 100)

        numbersObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinct()
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(integer: Int) {
                        Log.d(TAG, "Mathematical operator DISTINCT, onNext: " + integer)
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {

                    }
                })
    }
    //[END] methods for DISTINCT operator on primitive data type

    //[START] methods for TakeLast operator on primitive data type
    private fun operatorTakeLastExample() {
        Observable
                .range(1, 10)
                .takeLast(4)
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "Subscribed")
                    }

                    override fun onNext(integer: Int) {
                        Log.d(TAG, "Mathematical operator TakeLast, onNext: " + integer)
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {
                        Log.d(TAG, "Completed")
                    }
                })
    }
    //[END] methods for TakeLast operator on primitive data type

    //[START] methods for Take operator on primitive data type
    private fun operatorTakeExample() {
        Observable
                .range(1, 10)
                .take(4)
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "Subscribed")
                    }

                    override fun onNext(integer: Int) {
                        Log.d(TAG, "Mathematical operator TAKE, onNext: " + integer)
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {
                        Log.d(TAG, "Completed")
                    }
                })
    }
    //[END] methods for Take operator on primitive data type

    //[START] methods for SkipLast operator on primitive data type
    private fun operatorSkipLastExample() {
        Observable
                .range(1, 10)
                .skipLast(4)
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "Subscribed")
                    }

                    override fun onNext(integer: Int) {
                        Log.d(TAG, "Mathematical operator SkipLast, onNext: " + integer)
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {
                        Log.d(TAG, "Completed")
                    }
                })
    }
    //[END] methods for SkipLast operator on primitive data type

    //[START] methods for Skip operator on primitive data type
    private fun operatorSkipExample() {
        Observable
                .range(1, 10)
                .skip(4)
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "Subscribed")
                    }

                    override fun onNext(integer: Int) {
                        Log.d(TAG, "Mathematical operator SKIP, onNext: " + integer)
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {
                        Log.d(TAG, "Completed")
                    }
                })
    }
    //[END] methods for Skip operator on primitive data type

    //[START] methods for Filter operator on custom data type
    private fun operatorFilterExampleOnCustomDataType() {
        val userObservable = getUsersObservableWithNameAndGender()

        userObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter { user -> user.gender!!.equals("female", ignoreCase = true) }
                .subscribeWith(object : DisposableObserver<UserWithNameAndGender>() {
                    override fun onNext(user: UserWithNameAndGender) {
                        Log.e(TAG, "Mathematical operator Filter on custom object: " + user.name + ", " + user.gender)
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {

                    }
                })
    }


    private fun getUsersObservableWithNameAndGender(): Observable<UserWithNameAndGender> {
        val maleUsers = arrayOf("Mark", "John", "Trump", "Obama")
        val femaleUsers = arrayOf("Lucy", "Scarlett", "April")

        val users = ArrayList<UserWithNameAndGender>()

        for (name in maleUsers) {
            val user = UserWithNameAndGender()
            user.name = name
            user.gender = "male"

            users.add(user)
        }

        for (name in femaleUsers) {
            val user = UserWithNameAndGender()
            user.name = name
            user.gender = "female"

            users.add(user)
        }
        return Observable
                .create(ObservableOnSubscribe<UserWithNameAndGender> { emitter ->
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
    //[END] methods for Filter operator on custom data type

    //[START] methods for Filter operator on primitive data type
    private fun operatorFilterExample() {
        Observable
                .just(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .filter { integer -> integer % 2 == 0 }
                .subscribe(object : DisposableObserver<Int>() {
                    override fun onNext(integer: Int) {
                        Log.e(TAG, "Mathematical operator Filter, Even number is : " + integer)
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {

                    }
                })
    }
    //[END] methods for Filter operator on primitive data type

    //[START] methods for REDUCE operator on primitive data type
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
    //[END] methods for REDUCE operator on primitive data type

    //[START] methods for COUNT operator on custom data type
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
    //[END] methods for COUNT operator on custom data type

    //[START] methods for AVERAGE operator on primitive data type
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
    //[END] methods for AVERAGE operator on primitive data type

    //[START] methods for SUM operator on primitive data type
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
    //[END] methods for SUM operator on primitive data type

    //[START] methods for MIN operator on primitive data type
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
    //[END] methods for MIN operator on primitive data type

    //[START] methods for MAX operator on primitive data type
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
    //[END] methods for MAX operator on primitive data type
}

