package com.develop.`in`.come.comeinfrontbase.network

import android.app.Application
import com.develop.`in`.come.comeinfrontbase.util.Constants
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

class ChatApplication : Application() {

    var socket: Socket? = null
        private set

    init {
        try {
            socket = IO.socket(Constants.CHAT_SERVER_URL)
            println("Socket works!")
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }

    }
}