package com.develop.`in`.come.comeinfrontbase.fragments
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.SyncStateContract
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.activities.LoginActivity
import com.develop.`in`.come.comeinfrontbase.activities.MainActivity
import com.develop.`in`.come.comeinfrontbase.util.Constants
import java.util.concurrent.CopyOnWriteArrayList

class SettingsFragment : Fragment(){
    lateinit var mBtnLogout: Button
    lateinit var mSharedPreferences: SharedPreferences
    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.layout_settings, container, false)

        mBtnLogout = view.findViewById<View>(R.id.btn_logout) as Button
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)

        mBtnLogout.setOnClickListener{
            logout()
        }
        return view
    }

    @SuppressLint("CommitPrefEdits")
    fun logout(){
        val editor = mSharedPreferences.edit()
        editor.remove(Constants.PHONE)
        editor.remove(Constants.NAME)
        editor.remove(Constants.FIRSTNAME)
        editor.remove(Constants.TOKEN)
        editor.remove(Constants.EMAIL)
        editor.remove(Constants.LASTNAME)
        editor.remove(Constants.CURRENT_USER)
        editor.apply()
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
    }
}