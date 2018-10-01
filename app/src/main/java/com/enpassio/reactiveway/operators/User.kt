package com.enpassio.reactiveway.operators

data class User(var name: String? = null,
                var email: String? = null,
                var gender: String? = null,
                var address: Address? = null)

data class Address(var address: String? = null)
data class Person(var name: String? = null,
                  var age: Int? = null)

class UserWithNameAndGender(var name: String? = null,
                            var gender: String? = null)

class Note(id: Int, note: String) {
    var id: Int = 0
        internal set
    var note: String? = null
        internal set

    init {
        this.id = id
        this.note = note
    }

    override fun equals(obj: Any?): Boolean {
        if (obj === this) {
            return true
        }

        return if (obj !is Note) {
            false
        } else note!!.equals(obj.note!!, ignoreCase = true)

    }

    override fun hashCode(): Int {
        var hash = 3
        hash = 53 * hash + if (this.note != null) this.note!!.hashCode() else 0
        return hash
    }
}