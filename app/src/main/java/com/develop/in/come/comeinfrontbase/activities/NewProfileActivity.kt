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
    lateinit var mBtnContinue: Button

    lateinit var mSharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_new_profile)
        initViews()
    }

    fun initViews(){
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        mTiUsername = findViewById<View>(R.id.ti_username) as TextInputLayout
        mEtUsername = findViewById<View>(R.id.et_username) as EditText

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

        val request = Request.Builder()
            .url(Constants.BASE_URL + "/users/checkUsername")
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