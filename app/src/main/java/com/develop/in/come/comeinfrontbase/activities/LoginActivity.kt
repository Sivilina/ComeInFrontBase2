package com.develop.`in`.come.comeinfrontbase.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.app.Fragment;
import android.app.FragmentManager
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.fragments.auth.ChangePasswordDialog
import com.develop.`in`.come.comeinfrontbase.fragments.auth.LoginFragment
import com.develop.`in`.come.comeinfrontbase.fragments.auth.ResetPasswordDialog
import com.develop.`in`.come.comeinfrontbase.util.Constants
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity(), ResetPasswordDialog.Listener, ChangePasswordDialog.Listener {
    override fun onPasswordChanged() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    lateinit var mFragmentManager: FragmentManager
    lateinit var mFragmentTransaction: FragmentTransaction
    lateinit var mSharedPreferences: SharedPreferences

    //  private ResetPasswordDialog mResetPasswordDialog;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        var token = mSharedPreferences.getString(Constants.TOKEN,null)
        if (token != null) {
            //do something with back
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        if (savedInstanceState == null) {
            loadFragment()
        }
    }

    private fun loadFragment() {
     //   if (mLoginFragment == null) {
         //   }
        mFragmentManager = fragmentManager
        mFragmentTransaction = mFragmentManager.beginTransaction()
        mFragmentTransaction.replace(R.id.fragmentFrame, LoginFragment()).commit()

       // fragmentManager.beginTransaction().replace(R.id.fragmentFrame, mLoginFragment, LoginFragment.TAG).commit()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val data = intent.data!!.lastPathSegment
        Log.d(TAG, "onNewIntent: " + data!!)
        //        mResetPasswordDialog = (ResetPasswordDialog) getFragmentManager().findFragmentByTag(ResetPasswordDialog.TAG);
        //
        //        if (mResetPasswordDialog != null)
        //            mResetPasswordDialog.setToken(data);
    }


    override fun onPasswordReset(message: String) {
        showSnackBarMessage(message)
    }

    private fun showSnackBarMessage(message: String) {
        Snackbar.make(findViewById<View>(R.id.activity_login), message, Snackbar.LENGTH_SHORT).show()
    }

    companion object {


        val TAG = LoginActivity::class.java.simpleName
    }
}
