package com.develop.`in`.come.comeinfrontbase.activities

import android.content.Intent
import android.content.SharedPreferences
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.util.Constants
import com.google.gson.Gson


class StartActivity : AppCompatActivity() {
    lateinit var mBtnComeIn: Button
    lateinit var mSharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        mBtnComeIn = findViewById(R.id.btn_come_in)
        mBtnComeIn.setOnClickListener{
            val editor = mSharedPreferences!!.edit()
            editor.putString(Constants.APPID, "the app is installed")
            editor.apply()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
