package com.develop.`in`.come.comeinfrontbase.fragments


import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.SyncStateContract
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ShareActionProvider
import android.widget.TextView
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.util.Constants

class ProfileFragment: Fragment(){
    lateinit var mTvFullName: TextView
    lateinit var mTvPhone: TextView
    lateinit var mTvUsername: TextView
    lateinit var mSharedPreferences: SharedPreferences
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_profile, container, false)
        initViews(view)
        return view
    }

    fun initViews(v: View){
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        mTvFullName = v.findViewById<View>(R.id.tv_fullname ) as TextView
        mTvUsername = v.findViewById<View>(R.id.tv_username_in_profile ) as TextView
        mTvPhone = v.findViewById<View>(R.id.tv_phone ) as TextView

        mTvFullName.setText(mSharedPreferences.getString(Constants.FIRSTNAME + Constants.SECONDNAME,""))
        mTvPhone.setText(mSharedPreferences.getString(Constants.PHONE + Constants.EMAIL,""))
        mTvUsername.setText(mSharedPreferences.getString(Constants.NAME,""))
    }
}