package com.develop.`in`.come.comeinfrontbase.util

import android.text.TextUtils
import android.util.Patterns

class CheckValidity {

    companion object {
        fun validateFields(name: String): Boolean {

            return !TextUtils.isEmpty(name)
        }

        fun validateEmail(string: String): Boolean {

            return !(TextUtils.isEmpty(string) || !Patterns.EMAIL_ADDRESS.matcher(string).matches())
        }
    }

}