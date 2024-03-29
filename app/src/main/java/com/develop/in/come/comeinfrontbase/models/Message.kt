package com.develop.`in`.come.comeinfrontbase.models

class Message{
    var mType: Int = 0
    var mMessage: String? = null
    var mUsername: String? = null
    companion object {
        const val TYPE_MESSAGE = 0
        const val TYPE_LOG = 1
        const val TYPE_ACTION = 2



    }
    class Builder(private val mType: Int) {
        private var mUsername: String? = null
        private var mMessage: String? = null
        fun username(username: String): Builder {
            mUsername = username
            return this
        }
        fun message(message: String): Builder {
            mMessage = message
            return this
        }
        fun build(): Message {
            val message = Message()
            message.mType = mType
            message.mUsername = mUsername
            message.mMessage = mMessage
            return message
        }
    }



}