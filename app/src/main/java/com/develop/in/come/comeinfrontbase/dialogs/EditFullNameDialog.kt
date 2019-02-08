package com.develop.`in`.come.comeinfrontbase.dialogs

import android.app.Activity
import android.app.Dialog
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
import com.develop.`in`.come.comeinfrontbase.util.Constants
import com.google.gson.Gson
import java.lang.ClassCastException

class EditFullNameDialog: DialogFragment(){

    val TAG = "EditFullNameDialog"
    interface OnInputListener{
        fun sendInputFullName(input:String)
    }

    lateinit var mOnInputListener: OnInputListener
    lateinit var mEtInputFirstName: EditText
    lateinit var mEtInputLastName: EditText
    lateinit var mTvActionOk: TextView
    lateinit var mTvActionCancel: TextView
    lateinit var mSharedPreferences: SharedPreferences
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_edit_fullname,container,false)

        try{
            mOnInputListener = targetFragment as OnInputListener
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
        mTvActionCancel = v.findViewById(R.id.action_cancel)
        mTvActionOk = v.findViewById(R.id.action_ok)
        mEtInputFirstName = v.findViewById(R.id.input_first_name)
        mEtInputLastName = v.findViewById(R.id.input_last_name)

        mEtInputFirstName.setText(mSharedPreferences.getString(Constants.FIRSTNAME,""))
        mEtInputLastName.setText(mSharedPreferences.getString(Constants.LASTNAME,""))
        mTvActionCancel.setOnClickListener{
            println("onClick: closing dialog")
            dialog.dismiss()
        }

        mTvActionOk.setOnClickListener{
            val firstname = mEtInputFirstName.text.toString()
            val lastname = mEtInputLastName.text.toString()

            mOnInputListener.sendInputFullName("$firstname $lastname")
            if((!firstname.equals("")) && (!lastname.equals(""))){
                val editor = mSharedPreferences.edit()
                editor.putString(Constants.FIRSTNAME, firstname)
                editor.putString(Constants.LASTNAME,lastname)
                editor.apply()
            }
            dialog.dismiss()
        }
    }

}