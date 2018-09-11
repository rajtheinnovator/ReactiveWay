package com.enpassio.reactiveway

data class User(var name: String? = null,
                var email: String? = null,
                var gender: String? = null,
                var address: Address? = null)

data class Address(var address: String? = null)
data class Person(var name: String? = null,
                  var age: Int? = null)