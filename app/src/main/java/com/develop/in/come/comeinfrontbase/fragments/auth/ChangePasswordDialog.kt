package com.develop.`in`.come.comeinfrontbase.fragments.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.activities.LoginActivity
import com.develop.`in`.come.comeinfrontbase.activities.MainActivity
import com.develop.`in`.come.comeinfrontbase.models.Response
import com.develop.`in`.come.comeinfrontbase.models.User
import com.develop.`in`.come.comeinfrontbase.network.NetworkUtil
import com.develop.`in`.come.comeinfrontbase.util.CheckValidity.Companion.validateEmail
import com.develop.`in`.come.comeinfrontbase.util.CheckValidity.Companion.validateFields
import com.develop.`in`.come.comeinfrontbase.util.Constants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.io.IOException

class ChangePasswordDialog: DialogFragment() {

    interface Listener {
        fun onPasswordChanged()
    }
    val TAG = this::class.java.simpleName

    lateinit var mEtOldPassword: EditText
    lateinit var mEtNewPassword: EditText
    lateinit var mBtChangePassword: Button
    lateinit var mBtCancel: Button
    lateinit var mTvMessage: TextView
    lateinit var mTiOldPassword: TextInputLayout
    lateinit var mTiNewPassword: TextInputLayout
    lateinit var mProgressBar: ProgressBar

    lateinit var mSubscriptions: CompositeSubscription
    lateinit var mListner: Listener
    lateinit var mToken: String
    lateinit var mEmail: String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_change_password, container, false)
        mSubscriptions = CompositeSubscription()
        getData()
        initViews(view)
        return view
    }

    private fun getData() {

        val bundle = arguments

        mToken = bundle!!.getString(Constants.TOKEN)!!
        mEmail = bundle.getString(Constants.EMAIL)!!
    }

   override fun onAttach(context: Context) {
        super.onAttach(context)
        mListner = context as MainActivity
    }

    private fun initViews(v: View) {

     mEtOldPassword = v.findViewById(R.id.et_old_password) as EditText
     mEtNewPassword = v.findViewById(R.id.et_new_password) as EditText
     mTiOldPassword = v.findViewById(R.id.ti_old_password) as TextInputLayout
     mTiNewPassword = v.findViewById(R.id.ti_new_password) as TextInputLayout
     mTvMessage = v.findViewById(R.id.tv_message) as TextView
     mBtChangePassword = v.findViewById(R.id.btn_change_password) as Button
     mBtCancel = v.findViewById(R.id.btn_cancel) as Button
     mProgressBar = v.findViewById(R.id.progress) as ProgressBar

     mBtChangePassword.setOnClickListener { view -> changePassword() }
     mBtCancel.setOnClickListener { view -> dismiss() }
 }

  private fun changePassword() {

        setError();

        var oldPassword = mEtOldPassword.getText().toString();
        var newPassword = mEtNewPassword.getText().toString();

        var err = 0;

        if (!validateFields(oldPassword)) {

            err++;
            mTiOldPassword.setError("Password should not be empty !");
        }

        if (!validateFields(newPassword)) {

            err++;
            mTiNewPassword.setError("Password should not be empty !");
        }

        if (err == 0) {

            var user = User() as User;
            user.password = oldPassword;
            user.newPassword = newPassword;
            changePasswordProgress(user);
            mProgressBar.setVisibility(View.VISIBLE);

        }
    }

    private fun setError() {

        mTiOldPassword.error = null
        mTiNewPassword.error = null
    }
    private fun changePasswordProgress(user: User) {

        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).changePassword(mEmail,user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    fun handleResponse(response: Response) {

        mProgressBar.setVisibility(View.GONE);
        mListner.onPasswordChanged();
        dismiss();
    }

    fun handleError(error: Throwable) {

        mProgressBar.setVisibility(View.GONE);

        if (error is HttpException) {

            val gson = GsonBuilder().create() as Gson;

            try {

                var errorBody = (error as HttpException).response().errorBody().string();
                val response = gson.fromJson<Response>(errorBody, Response::class.java)
                showMessage(response.message!!.email!!)

            } catch ( e: IOException) {
                e.printStackTrace();
            }
        } else {

            showMessage("Network Error !");
        }
    }

    private fun showMessage(message: String) {

        mTvMessage.visibility = View.VISIBLE
        mTvMessage.text = message

    }


    override fun onDestroy() {
        super.onDestroy()
        mSubscriptions.unsubscribe()
    }

}