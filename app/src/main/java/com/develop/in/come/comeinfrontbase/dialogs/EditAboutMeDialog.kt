package com.develop.`in`.come.comeinfrontbase.dialogs

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.develop.`in`.come.comeinfrontbase.R
import java.lang.ClassCastException

    class EditAboutMeDialog: DialogFragment(){

    val TAG = "EditAboutMeDialog"
    interface OnInputListener{
        fun sendInputAboutMe(input:String)
    }

    var aboutMeSP:String?=null

    lateinit var mOnInputListener: OnInputListener
    lateinit var mEtAboutMe: EditText
    lateinit var mTvActionOk: TextView
    lateinit var mTvActionCancel: TextView
    lateinit var mSharedPreferences: SharedPreferences
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_edit_about_me,container,false)

        try{
            mOnInputListener = activity as OnInputListener
        }catch (e: ClassCastException){
            println("ClassCastException: ${e.message}")
        }
        initSharedPreferences()
        initviews(view)

        return view; }

    private fun initSharedPreferences() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
    }

    fun initviews(v: View){
        mTvActionCancel = v.findViewById(R.id.action_cancel) as TextView
        mTvActionOk = v.findViewById(R.id.action_ok) as TextView
        mEtAboutMe = v.findViewById(R.id.input_about_me) as EditText
        mEtAboutMe.setText(aboutMeSP)

        mTvActionCancel.setOnClickListener{
            println("onClick: closing dialog")
            dialog.dismiss()
        }

        mTvActionOk.setOnClickListener{
            val aboutMe = mEtAboutMe.text.toString()

            mOnInputListener.sendInputAboutMe(aboutMe)
            dialog.dismiss()
        }
    }

}