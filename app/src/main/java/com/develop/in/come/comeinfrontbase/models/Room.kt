package com.develop.`in`.come.comeinfrontbase.models

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import kotlin.String as String1


class Room {
    var roomid: String1? = null;
    var adminid: String1? = null
    var iconUrl: String1? = null
    var usersCount = 0;
    var title: String1? = null
    var description: String1? = null
    var roomType: String1? = null
    var createdAt: String1? = null
    var updatedAt: String1? = null
    var lastMessage: String1? = null
    var members: ArrayList<User> = ArrayList()
    val is_new: Boolean? = null
    val last_message_text: String? = null
    val status: Int = 0
    val sender: User? = null


    constructor(
        roomid: String1?,
        adminid: String1?,
        iconUrl: String1?,
        usersCount: Int,
        title: String1?,
        description: String1?,
        roomType: String1?,
        createdAt: String1?,
        updatedAt: String1?
    ) {
        this.roomid = roomid
        this.adminid = adminid
        this.iconUrl = iconUrl
        this.usersCount = usersCount
        this.title = title
        this.description = description
        this.roomType = roomType
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
    fun getDateTime():Long{
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        return df.parse(createdAt).time
    }

    override fun equals(other: Any?): Boolean {
        if (other is Room) {
            val conversation = other as Room?
            return if (this.roomid.equals(conversation!!.roomid)) true else false
        }

        return false
    }








}