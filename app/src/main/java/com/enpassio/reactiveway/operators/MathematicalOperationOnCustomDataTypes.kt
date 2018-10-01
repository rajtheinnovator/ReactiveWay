package com.enpassio.reactiveway.operators

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.enpassio.reactiveway.R
import rx.Observable
import rx.Observer
import rx.observables.MathObservable

class MathematicalOperationOnCustomDataTypes : AppCompatActivity() {


    companion object {

        private val TAG = MathematicalOperationOnCustomDataTypes::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.operators_activity_main)

        operatorOnCustomDataTypeExample()
    }

    private fun operatorOnCustomDataTypeExample() {
        val persons = ArrayList<Person>()
        persons.addAll(getPersons())

        val personObservable = Observable.from(persons)

        MathObservable.from(personObservable)
                .max(Comparator<Person> { person1, person2 -> person1.age!!.compareTo(person2.age!!) })
                .subscribe(object : Observer<Person> {

                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onNext(person: Person) {
                        Log.d(TAG, "Operator on Custom data type, Person with max age: " + person.name + ", " + person.age + " yrs")
                    }
                })
    }

    private fun getPersons(): List<Person> {
        val persons = ArrayList<Person>()

        val person1 = Person("person 1", 24)
        persons.add(person1)

        val person2 = Person("person 2", 45)
        persons.add(person2)

        val person3 = Person("person 3", 51)
        persons.add(person3)
        val person4 = Person("person 4", 21)
        persons.add(person4)
        val person5 = Person("person 5", 59)
        persons.add(person5)

        return persons
    }
}

