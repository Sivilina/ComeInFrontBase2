package com.develop.`in`.come.comeinfrontbase.fragments.auth

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.app.Fragment;
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.models.Response
import com.develop.`in`.come.comeinfrontbase.models.User
import com.develop.`in`.come.comeinfrontbase.network.NetworkUtil
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.io.IOException
import android.app.Activity.RESULT_OK
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.develop.`in`.come.comeinfrontbase.activities.MainActivity
import com.develop.`in`.come.comeinfrontbase.util.CheckValidity.Companion.validateEmail
import com.develop.`in`.come.comeinfrontbase.util.CheckValidity.Companion.validateFields
import com.develop.`in`.come.comeinfrontbase.util.Constants


class RegisterFragment:Fragment(){
    val TAG = RegisterFragment::class.java.simpleName

    lateinit var mEtName: EditText
    lateinit var mEtEmail: EditText
    lateinit var mEtPassword: EditText
    lateinit var mBtRegister: Button
    lateinit var mIvAvatar: ImageView
    lateinit var mBtUploadAvatar: Button
    lateinit var mTvLogin: TextView
    lateinit var mTiName: TextInputLayout
    lateinit var mTiEmail: TextInputLayout
    lateinit var mTiPassword: TextInputLayout
    lateinit var mProgressbar: ProgressBar
    private var mSharedPreferences: SharedPreferences? = null


    lateinit var mSubscriptions: CompositeSubscription

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_registration, container, false)
        mSubscriptions = CompositeSubscription()
        initViews(view)
        initSharedPreferences()
        return view
    }

    private fun initViews(v: View) {

        mEtName = v.findViewById<View>(R.id.et_name) as EditText
        mEtEmail = v.findViewById<View>(R.id.et_email) as EditText
        mEtPassword = v.findViewById<View>(R.id.et_password) as EditText
        mBtRegister = v.findViewById<View>(R.id.btn_register) as Button
        mTvLogin = v.findViewById<View>(R.id.tv_login) as TextView
        mTiEmail = v.findViewById<View>(R.id.ti_email) as TextInputLayout
        mTiPassword = v.findViewById<View>(R.id.ti_password) as TextInputLayout
        mProgressbar = v.findViewById<View>(R.id.progress) as ProgressBar
        mIvAvatar = v.findViewById<View>(R.id.iv_avatar) as ImageView
        mBtUploadAvatar = v.findViewById<View>(R.id.btn_upload_avatar) as Button
        mTiName = v.findViewById<View>(R.id.ti_name) as TextInputLayout

        mBtRegister.setOnClickListener { view -> register() }
        mTvLogin.setOnClickListener { view -> goToLogin() }
        mBtUploadAvatar.setOnClickListener { view -> uploadAvatar() }
    }

    private fun initSharedPreferences() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
    }
    private fun register() {

        setError()
        val name = mEtName.text.toString()
        val email = mEtEmail.text.toString()
        val password = mEtPassword.text.toString()

        var err = 0

        if (!validateFields(name)) {

            err++
            mTiName.error = "Name should not be empty !"
        }

        if (!validateEmail(email)) {

            err++
            mTiEmail.error = "Email should be valid !"
        }

        if (!validateFields(password)) {

            err++
            mTiPassword.error = "Password should not be empty !"
        }

        if (err == 0) {

            val user = User()
            user.name = name
            user.email = email
            user.password = password

            mProgressbar.visibility = View.VISIBLE
            registerProcess(user)

        } else {

            showSnackBarMessage("Enter Valid Details !")
        }
    }

    private fun setError() {

        mTiName.error = null
        mTiEmail.error = null
        mTiPassword.error = null
    }

    private fun registerProcess(user: User) {

        mSubscriptions.add(
            NetworkUtil.getRetrofit().register(user.email,user.password)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ this.handleResponse(it) }, { this.handleError(it) })
        )
    }

    private fun handleResponse(response: Response) {

        mProgressbar.visibility = View.GONE
        val editor = mSharedPreferences!!.edit()
        editor.putString(Constants.TOKEN, response.token)
        editor.putString(Constants.EMAIL, response.message!!.email)
        editor.putString(Constants.NAME, response.message!!.name)
        editor.apply()
        mEtEmail.setText(null)
        mEtPassword.setText(null)
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }

    private fun handleError(error: Throwable) {

        mProgressbar.visibility = View.GONE

        //if (error instanceof HttpException) {

        //Gson gson = new GsonBuilder().create();
        // String errorBody = ((HttpException) error).response().errorBody().string();
        // Response response = gson.fromJson(errorBody,Response.class);
        //      showSnackBarMessage(error.getMessage());
        //} else {
        println(error)
        showSnackBarMessage("Network Error !$error")
        //}
    }

    private fun showSnackBarMessage(message: String) {

        if (view != null) {

            Snackbar.make(view!!, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun goToLogin() {

        val ft = fragmentManager!!.beginTransaction()
        ft.replace(R.id.fragmentFrame, LoginFragment())
        ft.commit()
    }

    private fun uploadAvatar() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)

        var bitmap: Bitmap? = null
        when (requestCode) {
            1 -> if (resultCode == RESULT_OK) {
                val selectedImage = imageReturnedIntent.data
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(activity!!.getContentResolver(), selectedImage)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                mIvAvatar.background = null
                mIvAvatar.setImageBitmap(bitmap)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mSubscriptions.unsubscribe()
    }

}