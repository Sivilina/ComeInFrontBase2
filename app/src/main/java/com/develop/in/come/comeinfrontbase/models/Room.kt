package com.develop.`in`.come.comeinfrontbase.models

class Room {
    var roomid: String? = null;
    var adminid: String? = null
    var iconUrl: String? = null
    var usersCount = 0;
    var title: String? = null
    var description: String? = null
    var roomType: String? = null
    var createdAt: String? = null
    var updatedAt: String? = null
    var lastMessage: String? = null

    constructor(
        roomid: String?,
        adminid: String?,
        iconUrl: String?,
        usersCount: Int,
        title: String?,
        description: String?,
        roomType: String?,
        createdAt: String?,
        updatedAt: String?
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




}