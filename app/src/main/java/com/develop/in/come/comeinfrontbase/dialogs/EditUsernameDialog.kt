package com.develop.`in`.come.comeinfrontbase.dialogs

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.models.User
import com.develop.`in`.come.comeinfrontbase.util.Constants
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.ClassCastException

class EditUsernameDialog: DialogFragment(){

    val TAG = "EditUsernameDialog"
    interface OnInputListener{
        fun sendInputUsername(input:String)
    }

    var usernameSP :String?=null

    val MEDIA_TYPE = MediaType.parse("application/json")
    lateinit var mOnInputListener: OnInputListener
    lateinit var mEtUsername: EditText
    lateinit var mTvActionOk: TextView
    lateinit var mTvActionCancel: TextView
    lateinit var mSharedPreferences: SharedPreferences
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_edit_username,container,false)

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
        mEtUsername = v.findViewById(R.id.input_username) as EditText
        mEtUsername.setText(usernameSP)
        mTvActionCancel.setOnClickListener{
            println("onClick: closing dialog")
            dialog.dismiss()
        }

        mTvActionOk.setOnClickListener{
            val username = mEtUsername.text.toString()
            val client = OkHttpClient()
            val postdata = JSONObject()
            try {
                postdata.put("username", username)
            } catch (e: JSONException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            val body = RequestBody.create(
                MEDIA_TYPE,
                postdata.toString()
            )
            println("Debug:" + postdata.toString())
            val token = mSharedPreferences.getString(Constants.TOKEN,"")
            val request = Request.Builder()
                .url(Constants.BASE_URL + "users/profile/updateUsername")
                .addHeader("Authorization", "bearer $token")
                .put(body)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    val mMessage = e.message.toString()
                    println(mMessage)
                    showSnackBarMessage(mMessage)
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: okhttp3.Response) {

                    val mMessage = response.body().string()
                    println(mMessage)
                    if (response.isSuccessful()){
                        try {
                            mOnInputListener.sendInputUsername(username)
                            val gson = Gson()
                            val json = mSharedPreferences.getString(Constants.CURRENT_USER, "")
                            val currentUser = gson.fromJson<Any>(json, User::class.java) as User
                            currentUser.username = username
                            val editor = mSharedPreferences.edit()
                            editor.putString(Constants.CURRENT_USER, gson.toJson(currentUser))
                            editor.apply()
                            dialog.dismiss()
                        } catch (e: Exception){
                            e.printStackTrace();
                        }
                    } else {
                        showSnackBarMessage("Error: " + response.message())
                    }
                }
            })


        }
    }

    private fun showSnackBarMessage(message: String) {

        Snackbar.make(activity!!.findViewById(R.id.activity_profile_llayout), message, Snackbar.LENGTH_SHORT).show()
    }
}