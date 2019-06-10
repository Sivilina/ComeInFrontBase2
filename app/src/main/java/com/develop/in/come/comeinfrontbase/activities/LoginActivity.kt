package com.develop.`in`.come.comeinfrontbase.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import android.app.Fragment;
import android.app.FragmentManager
import android.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.fragments.auth.LoginFragment
import com.develop.`in`.come.comeinfrontbase.util.Constants
import kotlinx.android.synthetic.main.activity_login.*



class LoginActivity : AppCompatActivity() {

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
    /*    val appId = mSharedPreferences.getString(Constants.APPID,null)
        if(appId == null){
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
        }
*/
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



    private fun showSnackBarMessage(message: String) {
        Snackbar.make(findViewById<View>(R.id.activity_login), message, Snackbar.LENGTH_SHORT).show()
    }

    companion object {


        val TAG = LoginActivity::class.java.simpleName
    }
}
