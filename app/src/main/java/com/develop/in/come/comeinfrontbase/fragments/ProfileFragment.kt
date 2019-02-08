package com.develop.`in`.come.comeinfrontbase.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.activities.MainActivity
import com.develop.`in`.come.comeinfrontbase.dialogs.EditAboutMeDialog
import com.develop.`in`.come.comeinfrontbase.dialogs.EditEmailDialog
import com.develop.`in`.come.comeinfrontbase.dialogs.EditFullNameDialog
import com.develop.`in`.come.comeinfrontbase.models.User
import com.develop.`in`.come.comeinfrontbase.util.Constants
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

class ProfileFragment: Fragment(),EditFullNameDialog.OnInputListener,EditEmailDialog.OnInputListener,EditAboutMeDialog.OnInputListener{
    override fun sendInputAboutMe(input: String) {
        mTvAboutMe.text = input
    }

    override fun sendInputEmail(input: String) {
        mTvEmail.text = input
    }

    override fun sendInputFullName(input: String) {
        mTvFullName.text = input
    }

    lateinit var mTvFullName: TextView
    lateinit var mTvPhone: TextView
    lateinit var mTvUsername: TextView
    lateinit var mTvEmail: TextView
    lateinit var mTvAboutMe: TextView
    lateinit var mSharedPreferences: SharedPreferences
    lateinit var mBtnEditFullName: ImageButton
    lateinit var mBtnEditUsername: ImageButton
    lateinit var mBtnEditEmail: ImageButton
    lateinit var mBtnEditAboutMe: ImageButton
    lateinit var mBtnEditPhone: ImageButton
    val MEDIA_TYPE = MediaType.parse("application/json")
    lateinit var currentUser: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_profile, container, false)
        initViews(view)
        initButtons(view)
        return view
    }

    fun initViews(v: View){
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        mTvFullName = v.findViewById<View>(R.id.tv_fullname ) as TextView
        mTvUsername = v.findViewById<View>(R.id.tv_username_in_profile ) as TextView
        mTvPhone = v.findViewById<View>(R.id.tv_phone ) as TextView
        mTvEmail = v.findViewById<View>(R.id.tv_email) as TextView
        mTvAboutMe = v.findViewById<View>(R.id.tv_about_me) as TextView
        val gson = Gson()
        val json = mSharedPreferences.getString(Constants.CURRENT_USER, "")
        currentUser = gson.fromJson<Any>(json, User::class.java) as User
        val fulName = currentUser.firstname + " " + currentUser.lastname
        mTvFullName.text = fulName
        mTvPhone.text = currentUser.phone
        mTvUsername.text = currentUser.username
        mTvEmail.text = currentUser.email
        mTvAboutMe.text = currentUser.aboutMe
    }

    fun initButtons(v: View){
        mBtnEditFullName = v.findViewById<View>(R.id.ib_edit_fullname) as ImageButton
        mBtnEditUsername = v.findViewById<View>(R.id.ib_edit_username) as ImageButton
        mBtnEditPhone = v.findViewById<View>(R.id.ib_edit_phone) as ImageButton
        mBtnEditEmail = v.findViewById<View>(R.id.ib_edit_email) as ImageButton
        mBtnEditAboutMe = v.findViewById<View>(R.id.ib_edit_about_me) as ImageButton

        mBtnEditFullName.setOnClickListener{
            val fm = activity!!.supportFragmentManager
            val dialog = EditFullNameDialog()
            dialog.setTargetFragment(this, 0)
            dialog.show(fm, "EditFullNameDialog")
        }
        mBtnEditAboutMe.setOnClickListener{
            val fm = activity!!.supportFragmentManager
            val dialog = EditAboutMeDialog()
            dialog.setTargetFragment(this, 0)
            dialog.show(fm, "EditAboutMeDialog")
        }
        mBtnEditEmail.setOnClickListener{
            val fm = activity!!.supportFragmentManager
            val dialog = EditEmailDialog()
            dialog.setTargetFragment(this, 0)
            dialog.show(fm, "EditEmailDialog")
        }
        mBtnEditPhone.setOnClickListener{
            editPhone()
        }
        mBtnEditUsername.setOnClickListener{
            editUserName()
        }
    }
    fun editUserName(){
        showSnackBarMessage("Currently it is impossible")
    }

    fun editPhone(){
        showSnackBarMessage("Currently it is impossible")
    }

    private fun showSnackBarMessage(message: String) {

        if (view != null) {

            Snackbar.make(view!!, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        var needToChange = false
        if(!((currentUser.firstname + " " + currentUser.lastname).equals(mTvFullName.text)))
            needToChange = true
        if(!((currentUser.email + "").equals(mTvEmail.text)))
            needToChange = true
        if(!((currentUser.aboutMe + "").equals(mTvAboutMe.text)))
            needToChange = true

        if(needToChange)
            updateInfo(currentUser.username!!,mSharedPreferences.getString(Constants.FIRSTNAME,"")!!,
                        mSharedPreferences.getString(Constants.LASTNAME,"")!!,
                        mTvEmail.text.toString(), mTvAboutMe.text.toString())

        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)


    }

    fun updateInfo(username:String,firstname:String,lastname:String,email:String,aboutme:String){
        val token = mSharedPreferences.getString(Constants.TOKEN,"")
        println("This is token $token")
        val client = OkHttpClient()
        val postdataprofile = JSONObject()
        try {
            postdataprofile.put("firstname", firstname)
            postdataprofile.put("lastname",lastname)
            postdataprofile.put("email",email)
            postdataprofile.put("about",aboutme)
        } catch (e: JSONException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        val postdata = JSONObject()
        try{
            postdata.put("username",username)
            postdata.put("profile",postdataprofile)
        }catch (e:JSONException){
            e.printStackTrace()
        }

        val body = RequestBody.create(
            MEDIA_TYPE,
            postdata.toString()
        )
        println(postdata.toString())

        val request = Request.Builder()
            .url(Constants.BASE_URL + "/users")
            .addHeader("Authorization", "Bearer $token")
            .put(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val mMessage = e.message.toString()
                println(mMessage)
                handleError(mMessage);
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: okhttp3.Response) {

                val mMessage = response.body().string()
                println(mMessage)
                if (response.isSuccessful()){
                    try {
                        handleResponse(mMessage)
                    } catch (e: Exception){
                        e.printStackTrace();
                    }
                } else {
                    handleError(mMessage)
                }
            }
        })
    }
    private fun handleResponse(response: String) {
        println(response)
        val gson = Gson()
        val json = mSharedPreferences.getString(Constants.CURRENT_USER, "")
        val currentUser = gson.fromJson<Any>(json, User::class.java) as User
        val jsonResponse = JSONObject(response).getJSONObject("data")
        currentUser.username = jsonResponse.getJSONObject("user").getString("username")
        currentUser.firstname = jsonResponse.getJSONObject("user").getJSONObject("profile").getString("firstname")
        currentUser.lastname = jsonResponse.getJSONObject("user").getJSONObject("profile").getString("lastname")
        currentUser.email = jsonResponse.getJSONObject("user").getJSONObject("profile").getString("email")
        currentUser.aboutMe = jsonResponse.getJSONObject("user").getJSONObject("profile").getString("about")
        val editor = mSharedPreferences.edit()
        editor.putString(Constants.CURRENT_USER, gson.toJson(currentUser))
        editor.apply()
    }

    private fun handleError(error: String) {
        val json = JSONObject(error)
        println(error)
        showSnackBarMessage("Error: " + json.getString("message"))
      }



}