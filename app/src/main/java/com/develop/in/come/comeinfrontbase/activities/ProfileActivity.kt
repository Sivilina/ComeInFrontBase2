package com.develop.`in`.come.comeinfrontbase.activities

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.*
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.*
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.activities.MainActivity
import com.develop.`in`.come.comeinfrontbase.activities.OnBackPressedListener
import com.develop.`in`.come.comeinfrontbase.dialogs.EditAboutMeDialog
import com.develop.`in`.come.comeinfrontbase.dialogs.EditEmailDialog
import com.develop.`in`.come.comeinfrontbase.dialogs.EditFullNameDialog
import com.develop.`in`.come.comeinfrontbase.dialogs.EditUsernameDialog
import com.develop.`in`.come.comeinfrontbase.fragments.ProfileAvatarFragment
import com.develop.`in`.come.comeinfrontbase.fragments.ProfileFragment
import com.develop.`in`.come.comeinfrontbase.fragments.auth.LoginFragment
import com.develop.`in`.come.comeinfrontbase.models.User
import com.develop.`in`.come.comeinfrontbase.util.Constants
import com.develop.`in`.come.comeinfrontbase.util.GraphicsUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.layout_profile.*
import kotlinx.android.synthetic.main.nav_header.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.jvm.javaClass


class ProfileActivity: AppCompatActivity(), OnBackPressedListener{

    lateinit var currentUser: User

    lateinit var mSharedPreferences: SharedPreferences
    lateinit var mToolbar: Toolbar
    val MEDIA_TYPE = MediaType.parse("application/json")
    lateinit var mFragmentTransaction: FragmentTransaction

    lateinit var mAvatarFragment: ProfileAvatarFragment
    lateinit var mProfileFragment: ProfileFragment


    //image end
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_profile)
        initViews()
        initButtons()
        if (savedInstanceState == null) {
            loadFragment()
        }
    }


    fun initViews(){
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        mAvatarFragment = ProfileAvatarFragment();
        mProfileFragment = ProfileFragment();

        //image begin
        mToolbar = findViewById(R.id.toolbar_profile) as Toolbar
        mToolbar.setTitle("My profile")
        mToolbar.setNavigationIcon(R.drawable.ic_back)
        setSupportActionBar(mToolbar)

    }



    fun initButtons(){
        mToolbar.setNavigationOnClickListener(object : View.OnClickListener{
            override fun onClick(view: View) {
                finish()
            }
        })
    }

    private fun loadFragment() {
        //   if (mLoginFragment == null) {
        //   }
        mFragmentTransaction = supportFragmentManager.beginTransaction()
        mFragmentTransaction.add(R.id.fragmentAvatarFrame, mAvatarFragment);
        mFragmentTransaction.add(R.id.fragmentProfileMainFrame, mProfileFragment);
        mFragmentTransaction.commit();

        // fragmentManager.beginTransaction().replace(R.id.fragmentFrame, mLoginFragment, LoginFragment.TAG).commit()
    }

    private fun showSnackBarMessage(message: String) {
            Snackbar.make(findViewById(R.id.activity_profile_llayout), message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

    }

    fun updateInfo(username:String,firstname:String,lastname:String,email:String,aboutme:String){
        val token = mSharedPreferences.getString(Constants.TOKEN,"")
        println("Debug: This is token $token")
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
                    handleError(response.toString())
                }
            }
        })
    }
    private fun handleResponse(response: String) {
        println(response)
        val gson = Gson()
        val json = mSharedPreferences.getString(Constants.CURRENT_USER, "")
        currentUser = gson.fromJson<Any>(json, User::class.java) as User
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
     //   val json = JSONObject(error)
        println(error)
        showSnackBarMessage("Error: " + error)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_camera,menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var id = item!!.itemId
        if (id == R.id.btn_save){
            var needToChange = false
            if(!((currentUser.firstname + " " + currentUser.lastname).equals(mProfileFragment.getFullName())))
                needToChange = true
            if(!((currentUser.email + "").equals(mProfileFragment.getEmail())))
                needToChange = true
            if(!((currentUser.aboutMe + "").equals(mProfileFragment.getAboutMe())))
                needToChange = true

            if(needToChange)
                updateInfo(currentUser.username!!,mSharedPreferences.getString(Constants.FIRSTNAME,"")!!,
                    mSharedPreferences.getString(Constants.LASTNAME,"")!!,
                    mProfileFragment.getEmail()!!, mProfileFragment.getAboutMe()!!)
        }
        return super.onOptionsItemSelected(item)
    }




}