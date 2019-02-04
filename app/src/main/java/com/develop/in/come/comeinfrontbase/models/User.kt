package com.develop.`in`.come.comeinfrontbase.models

class User {

    var name: String? = null
    var email: String? = null
    var password: String? = null
    var created_at: String? = null
    var newPassword: String? = null
    var token: String? = null

    constructor()

    constructor(u: User) {
        this.name = u.name
        this.email = u.email
        this.password = u.password
        this.created_at = u.created_at
        this.token = u.token
    }

    constructor(email: String, password: String) {
        this.email = email
        this.password = password
    }


    override fun toString(): String {
        return "{" +
                "email:'" + email + '\''.toString() +
                ", name:'" + name + '\''.toString() +
                '}'.toString()
    }

}