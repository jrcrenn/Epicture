package com.example.epicture

class User {
    lateinit var data : UserData
}

class UserData {
    var url : String? = null
    var id : String? = null
    var avatar : String? = null
    val reputation_name: String? = null
}