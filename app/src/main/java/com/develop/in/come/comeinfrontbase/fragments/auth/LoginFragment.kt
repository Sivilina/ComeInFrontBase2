package com.develop.`in`.come.comeinfrontbase.fragments.auth

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import android.app.Fragment;
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.activities.MainActivity
import com.develop.`in`.come.comeinfrontbase.activities.NewProfileActivity
import com.develop.`in`.come.comeinfrontbase.util.CheckValidity.Companion.validateFields
import com.develop.`in`.come.comeinfrontbase.util.Constants
import java.io.IOException
import com.develop.`in`.come.comeinfrontbase.models.User
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import com.hbb20.CountryCodePicker



class LoginFragment: Fragment() {

    lateinit var mEtPhone: EditText
    lateinit var mEtPassword: EditText
    lateinit var mBtLogin: Button
    lateinit var mTiPassword: TextInputLayout
    lateinit var mProgressBar: ProgressBar
    private var mSharedPreferences: SharedPreferences? = null
    lateinit var ccp: CountryCodePicker
    val MEDIA_TYPE = MediaType.parse("application/json")



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        initViews(view)
        initSharedPreferences()
        return view
    }

    private fun initViews(v: View) {
        ccp = v.findViewById(R.id.ccp) as CountryCodePicker
        mEtPhone = v.findViewById(R.id.et_phone) as EditText
        ccp.registerCarrierNumberEditText(mEtPhone);
        mEtPassword = v.findViewById(R.id.et_password) as EditText
        mBtLogin = v.findViewById(R.id.btn_login) as Button
        mTiPassword = v.findViewById(R.id.ti_password) as TextInputLayout
        mProgressBar = v.findViewById(R.id.progress) as ProgressBar

        mBtLogin.setOnClickListener { view -> login() }
        ccp.setPhoneNumberValidityChangeListener(CountryCodePicker.PhoneNumberValidityChangeListener() {
            fun onValidityChanged(isValidNumber: Boolean) {
                if(!isValidNumber)
                    showSnackBarMessage("Phone is invalid")
            }
        });
      }

    private fun initSharedPreferences() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
    }

    private fun login() {
        println("Debug: checking fields")
        setError()
        val phone = mEtPhone.text.toString()
        val password = mEtPassword.text.toString()

        var err = 0

        if (!ccp.isValidFullNumber) {

            err++
            showSnackBarMessage("Phone is invalid")
        }

        if (!validateFields(password)) {

            err++
            mTiPassword.error = "Code should not be empty !"
        }

        if (err == 0) {

            loginProcess(ccp.getFullNumberWithPlus(), password)
            println("Debug: phone " + ccp.fullNumberWithPlus)

        } else {

            showSnackBarMessage("Enter Valid Details !")
        }
    }

    private fun setError() {

        mTiPassword.error = null
    }

    private fun loginProcess(phone: String, password: String) {
        print("Debug: logining..")
        val client = OkHttpClient()
        val postdata = JSONObject()
        try {
            postdata.put("phone", phone)
            postdata.put("password", password)
        } catch (e: JSONException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        val body = RequestBody.create(
            MEDIA_TYPE,
            postdata.toString()
        )

        val request = Request.Builder()
            .url(Constants.BASE_URL + "auth")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val mMessage = e.message.toString()
                println(mMessage)
                showSnackBarMessage(mMessage)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: okhttp3.Response) {

                val mMessage = response.body()!!.string()
                println(mMessage)

                if (response.isSuccessful()){
                    try {

                        handleResponse(mMessage,response.code())
                    } catch (e: Exception){
                        e.printStackTrace();
                    }
                } else {
                    handleError(response.toString())
                }
            }
        })
    }

    private fun handleResponse(response: String, status: Int) {
        println(response)
        println("Status Code: $status")
        val json = JSONObject(response)
        val data = json.getJSONObject("data")
        val user = User()
        user.token = data.getString("token")
        user.phone = data.getJSONObject("user").getString("phone")
        user.created_at = data.getJSONObject("user").getString("createdAt")
        var username = data.getJSONObject("user").getString("username")
        if(username == "null"){
            println("Unauthorised")
            addUserToSharedPreference(user)
            val intent = Intent(activity, NewProfileActivity::class.java)
            startActivity(intent)
        } else {
            println("Authorised")
            user.username = data.getJSONObject("user").getString("username")
            val profile = data.getJSONObject("user").getJSONObject("profile")
            user.firstname = profile.getString("firstname")
            user.lastname = profile.getString("lastname")
            user.email = profile.getString("email")
            user.aboutMe = profile.getString("about")
            addUserToSharedPreference(user)
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity.finish()
        }
        println("Debug: Token: ${user.token}")


    }


    fun addUserToSharedPreference(u:User){
        val editor = mSharedPreferences!!.edit()
        val gson = Gson()
        editor.putString(Constants.CURRENT_USER, gson.toJson(u))
        editor.putString(Constants.TOKEN,u.token)
        if(!u.firstname.isNullOrEmpty())
            editor.putString(Constants.FIRSTNAME, u.firstname)
        if(!u.lastname.isNullOrEmpty())
            editor.putString(Constants.LASTNAME,u.lastname)
        editor.apply()
        showSnackBarMessage(u.toString())
    }

    private fun handleError(error: String) {
        val json = JSONObject(error)
        println(error)
        showSnackBarMessage("Error: " + json.getJSONObject("error").getString("message"))
    }

    private fun showSnackBarMessage(message: String) {

        if (view != null) {

            Snackbar.make(view!!, message, Snackbar.LENGTH_SHORT).show()
        }
    }



    override fun onDestroy() {
        super.onDestroy()
    }

}