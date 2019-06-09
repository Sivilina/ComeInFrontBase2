package com.develop.`in`.come.comeinfrontbase.activities


import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.models.User
import com.develop.`in`.come.comeinfrontbase.util.CheckValidity
import com.develop.`in`.come.comeinfrontbase.util.Constants
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception


class NewProfileActivity: Activity(){
    val MEDIA_TYPE = MediaType.parse("application/json")

    lateinit var mTiUsername: TextInputLayout
    lateinit var mEtUsername: EditText
    lateinit var mTiFirstname: TextInputLayout
    lateinit var mEtFirstname: EditText
    lateinit var mTiLastname: TextInputLayout
    lateinit var mEtLastname: EditText
    lateinit var mTiEmail: TextInputLayout
    lateinit var mEtEmail: EditText
    lateinit var mTiAbout: TextInputLayout
    lateinit var mEtAbout: EditText
    lateinit var mBtnContinue: Button


    lateinit var mSharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_new_profile)
        initViews()
    }

    fun initViews(){
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        mTiUsername = findViewById(R.id.ti_username) as TextInputLayout
        mEtUsername = findViewById(R.id.et_username) as EditText
        mTiFirstname = findViewById(R.id.ti_firstname) as TextInputLayout
        mEtFirstname = findViewById(R.id.et_firstname) as EditText
        mTiLastname = findViewById(R.id.ti_lastname) as TextInputLayout
        mEtLastname = findViewById(R.id.et_lastname) as EditText
        mTiEmail = findViewById(R.id.ti_email) as TextInputLayout
        mEtEmail = findViewById(R.id.et_email) as EditText
        mTiAbout = findViewById(R.id.ti_about) as TextInputLayout
        mEtAbout = findViewById(R.id.et_about) as EditText

        mBtnContinue = findViewById(R.id.btn_continue_reg) as Button
        mBtnContinue.setOnClickListener{
            editProfile()
        }


    }

    fun editProfile(){

        setError()

        val username = mEtUsername.text.toString()

        var err = 0

        if (!CheckValidity.validateFields(username)) {

            err++
            mTiUsername.error = "Username should not be empty!"
        }

        if (err == 0) {

            register(username)

        }
    }
    private fun setError() {
        mTiUsername.error = null
    }

    fun register(username:String){
        val token = mSharedPreferences.getString(Constants.TOKEN,"")
        println("This is token $token")
        val client = OkHttpClient()
        val postdataprofile = JSONObject()
        try {
            postdataprofile.put("firstname", mEtFirstname.text!!)
            postdataprofile.put("lastname",mEtLastname.text!!)
            postdataprofile.put("email",mEtEmail.text!!)
            postdataprofile.put("about",mEtAbout.text!!)
        } catch (e: JSONException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        val postdata = JSONObject()
        try{
            postdata.put("username",username)
            postdata.put("profile",postdataprofile)
        }catch (e: JSONException){
            e.printStackTrace()
        }

        val body = RequestBody.create(
            MEDIA_TYPE,
            postdata.toString()
        )
        println("Debug:" + postdata.toString())
        val request = Request.Builder()
            .url(Constants.BASE_URL + "users/profile")
            .addHeader("Authorization", "bearer $token")
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
        var profile = jsonResponse.getJSONObject("user").getJSONObject("profile")
        if(profile.has("firstname"))
            currentUser.firstname = profile.getString("firstname")
        if(profile.has("lastname"))
            currentUser.lastname = profile.getString("lastname")
        if(profile.has("email"))
            currentUser.email = profile.getString("email")
        if(profile.has("about"))
            currentUser.aboutMe = profile.getString("about")
        val editor = mSharedPreferences.edit()
        editor.putString(Constants.CURRENT_USER, gson.toJson(currentUser))
        editor.apply()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun handleError(error: String) {
        val json = JSONObject(error)
        showSnackBarMessage("Error: " + json.getString("message"))
    }

    private fun showSnackBarMessage(message: String) {
        Snackbar.make(findViewById(R.id.new_profile), message, Snackbar.LENGTH_SHORT).show()
    }


}