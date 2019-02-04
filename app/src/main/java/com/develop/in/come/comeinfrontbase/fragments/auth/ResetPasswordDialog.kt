package com.develop.`in`.come.comeinfrontbase.fragments.auth



import android.app.DialogFragment;
import android.app.FragmentManager
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.GestureDetector
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.activities.LoginActivity
import com.develop.`in`.come.comeinfrontbase.models.Response
import com.develop.`in`.come.comeinfrontbase.models.User
import com.develop.`in`.come.comeinfrontbase.network.NetworkUtil
import com.develop.`in`.come.comeinfrontbase.util.CheckValidity.Companion.validateEmail
import com.develop.`in`.come.comeinfrontbase.util.CheckValidity.Companion.validateFields

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;



class ResetPasswordDialog : DialogFragment() {

    interface Listener {

        fun onPasswordReset(message:String)
    }

    val TAG = this::class.java.simpleName

    override fun show(manager: FragmentManager, tag: String) {
        super.show(manager, tag)
    }

    lateinit var mEtEmail: EditText
    lateinit var mEtToken: EditText
    lateinit var mEtPassword: EditText
    lateinit var mBtResetPassword: Button
    lateinit var mTvMessage: TextView
    lateinit var mTiEmail: TextInputLayout
    lateinit var mTiToken: TextInputLayout
    lateinit var mTiPassword: TextInputLayout
    lateinit var mProgressBar: ProgressBar
    lateinit var mSubscriptions: CompositeSubscription
    lateinit var mEmail: String

    var isInit = true;

    lateinit var mListner: Listener

    @Nullable
    @Override
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_reset_password,container,false);
        mSubscriptions = CompositeSubscription()
        initViews(view);
        return view;

    }

    fun initViews(v: View){
        mEtEmail = v.findViewById<View>(R.id.et_email) as EditText
        mEtPassword = v.findViewById<View>(R.id.et_password) as EditText
        mBtResetPassword = v.findViewById(R.id.btn_reset_password) as Button
        mTiEmail = v.findViewById<View>(R.id.ti_email) as TextInputLayout
        mTiPassword = v.findViewById<View>(R.id.ti_password) as TextInputLayout
        mProgressBar = v.findViewById<View>(R.id.progress) as ProgressBar
        mTvMessage = v.findViewById(R.id.tv_message) as TextView

        mBtResetPassword.setOnClickListener{
            if (isInit) resetPasswordInit();
            else resetPasswordFinish();
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListner = context as LoginActivity;

    }


    fun setEmptyFields() {
        mTiEmail.setError(null);
        mTiToken.setError(null);
        mTiPassword.setError(null);
        mTvMessage.setText(null);
    }

    fun setToken(token: String) {
        mEtToken.setText(token);
    }

    fun resetPasswordInit() {

        setEmptyFields();
        mEmail = mEtEmail.getText().toString();
        var err = 0;
        if (!validateEmail(mEmail)) {
            err++;
            mTiEmail.setError("Email Should be Valid !");
        }

        if (err == 0) {
            mProgressBar.setVisibility(View.VISIBLE);
            resetPasswordInitProgress(mEmail);
        }
    }

    fun resetPasswordFinish() {

        setEmptyFields();
        var token = mEtToken.getText().toString();
        var password = mEtPassword.getText().toString();
        var err = 0;

        if (!validateFields(token)) {
            err++;
            mTiToken.setError("Token Should not be empty !");
        }

        if (!validateFields(password)) {
            err++;
            mTiEmail.setError("Password Should not be empty !");
        }

        if (err == 0) {
            mProgressBar.setVisibility(View.VISIBLE);
            var user = User();
            user.password = password;
            user.token = token;
            resetPasswordFinishProgress(user);
        }
    }

    fun resetPasswordInitProgress(email: String) {

        mSubscriptions.add(NetworkUtil.getRetrofit().resetPasswordInit(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    fun resetPasswordFinishProgress(user: User) {

        mSubscriptions.add(NetworkUtil.getRetrofit().resetPasswordFinish(mEmail,user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    fun handleResponse(response: Response) {

        mProgressBar.setVisibility(View.GONE);
        if (isInit) {
            isInit = false;
            showMessage(response.message!!.name!!);
            mTiEmail.setVisibility(View.GONE);
            mTiToken.setVisibility(View.VISIBLE);
            mTiPassword.setVisibility(View.VISIBLE);

        } else {
            mListner.onPasswordReset(response.message!!.email!!);
            dismiss();
        }
    }

    fun handleError(error: Throwable) {

        mProgressBar.setVisibility(View.GONE);

        if (error is HttpException) {

            var gson = GsonBuilder().create() as Gson;

            try {

                var errorBody = (error as HttpException).response().errorBody().string();
                val response = gson.fromJson<Response>(errorBody, Response::class.java)
                showMessage(response.message!!.email!!)

            } catch (e: IOException ) {
                e.printStackTrace();
            }
        } else {

            showMessage("Network Error !");
        }
    }

    fun showMessage(message: String) {

        mTvMessage.setVisibility(View.VISIBLE);
        mTvMessage.setText(message);

    }

    override fun onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}