package com.develop.`in`.come.comeinfrontbase.activities

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.activities.MainActivity
import com.develop.`in`.come.comeinfrontbase.activities.OnBackPressedListener
import com.develop.`in`.come.comeinfrontbase.dialogs.EditAboutMeDialog
import com.develop.`in`.come.comeinfrontbase.dialogs.EditEmailDialog
import com.develop.`in`.come.comeinfrontbase.dialogs.EditFullNameDialog
import com.develop.`in`.come.comeinfrontbase.models.User
import com.develop.`in`.come.comeinfrontbase.util.Constants
import com.develop.`in`.come.comeinfrontbase.util.GraphicsUtil
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import kotlin.jvm.javaClass


class ProfileActivity: AppCompatActivity(), EditFullNameDialog.OnInputListener, EditEmailDialog.OnInputListener,
    EditAboutMeDialog.OnInputListener,OnBackPressedListener{


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
    //image begin
    lateinit var mIvAvatar: ImageView
    lateinit var mToolbar: Toolbar
    lateinit var file: File
    lateinit var uri: Uri
    lateinit var camIntent: Intent
    lateinit var galIntent: Intent
    lateinit var cropIntent: Intent
    lateinit var mIbUploadCamera: ImageButton
    lateinit var mIbUploadGallery: ImageButton
    val RequestPermissionCode=1
    val REQUEST_CODE_GALLERY = 0x1
    val REQUEST_CODE_TAKE_PICTURE = 0x2
    val REQUEST_CODE_CROP_IMAGE = 0x3

    //image end
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_profile)
        initViews()
        initButtons()
    }


    fun initViews(){
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        mTvFullName = findViewById<View>(R.id.tv_fullname ) as TextView
        mTvUsername = findViewById<View>(R.id.tv_username_in_profile ) as TextView
        mTvPhone = findViewById<View>(R.id.tv_phone ) as TextView
        mTvEmail = findViewById<View>(R.id.tv_email) as TextView
        mTvAboutMe = findViewById<View>(R.id.tv_about_me) as TextView

        val gson = Gson()
        val json = mSharedPreferences.getString(Constants.CURRENT_USER, "")
        currentUser = gson.fromJson<Any>(json, User::class.java) as User
        val fulName = currentUser.firstname + " " + currentUser.lastname
        mTvFullName.text = fulName
        mTvPhone.text = currentUser.phone
        mTvUsername.text = currentUser.username
        mTvEmail.text = currentUser.email
        mTvAboutMe.text = currentUser.aboutMe
        //image begin
        mToolbar = findViewById(R.id.toolbar_profile) as Toolbar
        mToolbar.setTitle("My profile")
        mToolbar.setNavigationIcon(R.drawable.ic_back)
        setSupportActionBar(mToolbar)
        mIvAvatar = findViewById(R.id.iv_profile_avatar) as ImageView
        var permissionCheck = ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)
        if(permissionCheck == PackageManager.PERMISSION_DENIED)
            RequestRuntimePermission()
        val state = Environment.getExternalStorageState()
        //image end

    }



    fun initButtons(){
        mBtnEditFullName = findViewById<View>(R.id.ib_edit_fullname) as ImageButton
        mBtnEditUsername = findViewById<View>(R.id.ib_edit_username) as ImageButton
        mBtnEditPhone = findViewById<View>(R.id.ib_edit_phone) as ImageButton
        mBtnEditEmail = findViewById<View>(R.id.ib_edit_email) as ImageButton
        mBtnEditAboutMe = findViewById<View>(R.id.ib_edit_about_me) as ImageButton
        mIbUploadCamera = findViewById<View>(R.id.ib_upload_avatar_camera) as ImageButton
        mIbUploadGallery = findViewById<View>(R.id.ib_upload_avatar_gallery) as ImageButton


        mBtnEditFullName.setOnClickListener{
            val dialog = EditFullNameDialog()
            dialog.show(supportFragmentManager,"EditFullNameDialog")
        }
        mBtnEditAboutMe.setOnClickListener{
            val dialog = EditAboutMeDialog()
            dialog.aboutMeSP = mTvAboutMe.text.toString()
            dialog.show(supportFragmentManager, "EditAboutMeDialog")
        }
        mBtnEditEmail.setOnClickListener{
            val dialog = EditEmailDialog()
            dialog.emailSP = mTvEmail.text.toString()
            dialog.show(supportFragmentManager, "EditEmailDialog")
        }
        mBtnEditPhone.setOnClickListener{
            editPhone()
        }
        mBtnEditUsername.setOnClickListener{
            editUserName()
        }

        mIbUploadCamera.setOnClickListener {
            CameraOpen()
        }
        mIbUploadGallery.setOnClickListener {
            GalleryOpen()
        }
        mToolbar.setNavigationOnClickListener(object : View.OnClickListener{
            override fun onClick(view: View) {
                finish()
            }
        })
    }

    fun editUserName(){
        showSnackBarMessage("Currently it is impossible")
    }

    fun editPhone(){
        showSnackBarMessage("Currently it is impossible")
    }

    private fun showSnackBarMessage(message: String) {

            Snackbar.make(findViewById(R.id.activity_profile_llayout), message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
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

        val intent = Intent(this, MainActivity::class.java)
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
        }catch (e: JSONException){
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

    private fun RequestRuntimePermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.CAMERA))
            Toast.makeText(this,"Allow Camera Permission", Toast.LENGTH_SHORT).show()
        else
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),RequestPermissionCode)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_camera,menu)
        return true
    }


    private fun GalleryOpen() {
        galIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(Intent.createChooser(galIntent,"Select Image from Gallery"),2)
    }

    private fun CameraOpen() {

        camIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)

        file = File(
            Environment.getExternalStorageDirectory(),
            "file" + System.currentTimeMillis().toString() + ".jpg"
        )
        uri = Uri.fromFile(file)

        camIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri)

        camIntent.putExtra("return-data", true)

        startActivityForResult(camIntent, 0)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 0 && resultCode == Activity.RESULT_OK)
            CropImage()
        else if(requestCode == 2){
            if(data != null){
                uri = data.data!!
                CropImage()
            }
        } else if(requestCode == 1){
             var bundle = data!!.getExtras();
             var bitmap = bundle.getParcelable("data") as Bitmap
             val graphicUtil = GraphicsUtil();
             mIvAvatar.setImageBitmap(graphicUtil.getRoundedShape(bitmap));
             //mIvAvatar.setImageBitmap(bitmap)
        }
    }

    private fun CropImage() {
        try{
            cropIntent = Intent("com.android.camera.action.CROP")
            cropIntent.setDataAndType(uri,"image/*")
            cropIntent.putExtra("crop","true")
            cropIntent.putExtra("aspectX", 1)
            cropIntent.putExtra("aspectY", 1)
            intent.putExtra("outputX", 1024);
            intent.putExtra("outputY", 1024);
            cropIntent.putExtra("return-data",true)

            startActivityForResult(cropIntent,1)
        } catch (e: ActivityNotFoundException){
            println(e)
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            RequestPermissionCode -> {
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this,"Permission granted", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(this,"Permission canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }

}