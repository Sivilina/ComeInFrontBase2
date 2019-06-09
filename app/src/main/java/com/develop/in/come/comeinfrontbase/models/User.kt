package com.develop.`in`.come.comeinfrontbase.models

class User {

    var userid: String? = null
    var firstname: String? = null
    var lastname: String? = null
    var username: String? = null
    var email: String? = null
    var phone: String? = null
    var password: String? = null
    var created_at: String? = null
    var newPassword: String? = null
    var token: String? = null
    var aboutMe: String? = null

    constructor()

    constructor(u: User) {
        this.phone = u.phone
        this.password = u.password
        this.created_at = u.created_at
        this.token = u.token
        this.userid = u.userid
    }

    constructor(phone: String, password: String) {
        this.phone = phone
        this.password = password
    }


}