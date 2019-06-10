package com.develop.`in`.come.comeinfrontbase.fragments

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.dialogs.EditAboutMeDialog
import com.develop.`in`.come.comeinfrontbase.dialogs.EditEmailDialog
import com.develop.`in`.come.comeinfrontbase.dialogs.EditFullNameDialog
import com.develop.`in`.come.comeinfrontbase.dialogs.EditUsernameDialog
import com.develop.`in`.come.comeinfrontbase.models.User
import com.develop.`in`.come.comeinfrontbase.util.Constants
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProfileFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ProfileFragment : androidx.fragment.app.Fragment() , EditFullNameDialog.OnInputListener, EditEmailDialog.OnInputListener,
EditAboutMeDialog.OnInputListener, EditUsernameDialog.OnInputListener{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

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

    override fun sendInputUsername(input: String) {
        mTvUsername.text = input
    }

    override fun sendInputAboutMe(input: String) {
        mTvAboutMe.text = input
    }

    override fun sendInputEmail(input: String) {
        mTvEmail.text = input
    }

    override fun sendInputFullName(input: String) {
        mTvFullName.text = input
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        initViews(view)
        initButtons(view)
        uploadInfo()
        return view
    }

    fun initViews(v: View) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        mTvFullName = v.findViewById<View>(R.id.tv_fullname) as TextView
        mTvUsername = v.findViewById<View>(R.id.tv_username_in_profile) as TextView
        mTvPhone = v.findViewById<View>(R.id.tv_phone) as TextView
        mTvEmail = v.findViewById<View>(R.id.tv_email) as TextView
        mTvAboutMe = v.findViewById<View>(R.id.tv_about_me) as TextView

        val gson = Gson()
        val json = mSharedPreferences.getString(Constants.CURRENT_USER, "")
        currentUser = gson.fromJson<Any>(json, User::class.java) as User
        //  uploadInfo()

    }

    fun initButtons(v:View){
        mBtnEditFullName = v.findViewById<View>(R.id.ib_edit_fullname) as ImageButton
        mBtnEditUsername = v.findViewById<View>(R.id.ib_edit_username) as ImageButton
        mBtnEditPhone = v.findViewById<View>(R.id.ib_edit_phone) as ImageButton
        mBtnEditEmail = v.findViewById<View>(R.id.ib_edit_email) as ImageButton
        mBtnEditAboutMe = v.findViewById<View>(R.id.ib_edit_about_me) as ImageButton

        mBtnEditFullName.setOnClickListener{
            val dialog = EditFullNameDialog()
            dialog.firstnameSP = currentUser.firstname
            dialog.lastnameSP = currentUser.lastname
            dialog.show(activity!!.supportFragmentManager,"EditFullNameDialog")
        }
        mBtnEditAboutMe.setOnClickListener{
            val dialog = EditAboutMeDialog()
            dialog.aboutMeSP = mTvAboutMe.text.toString()
            dialog.show(activity!!.supportFragmentManager, "EditAboutMeDialog")
        }
        mBtnEditEmail.setOnClickListener{
            val dialog = EditEmailDialog()
            dialog.emailSP = mTvEmail.text.toString()
            dialog.show(activity!!.supportFragmentManager, "EditEmailDialog")
        }
        mBtnEditPhone.setOnClickListener{
            editPhone()
        }
        mBtnEditUsername.setOnClickListener{
            val dialog = EditUsernameDialog()
            dialog.usernameSP = mTvUsername.text.toString()
            dialog.show(activity!!.supportFragmentManager, "EditUsernameDialog")
        }
    }
    fun editPhone(){
        showSnackBarMessage("Currently it is impossible")
    }
    fun uploadInfo(){
        val token = mSharedPreferences.getString(Constants.TOKEN,"")
        println("Debug: This is token $token")
        val client = OkHttpClient()


        val request = Request.Builder()
            .url(Constants.BASE_URL + "users/profile")
            .addHeader("Authorization", "bearer $token")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val mMessage = e.message.toString()
                println(mMessage)
                handleError(mMessage);
            }

            @RequiresApi(Build.VERSION_CODES.GINGERBREAD)
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: okhttp3.Response) {

                val mMessage = response.body()!!.string()
                println(mMessage)
                if (response.isSuccessful()){
                    try {
                        //  handleResponse(mMessage)
                        val jsonResponse = JSONObject(mMessage).getJSONObject("data")
                        mTvUsername.text = jsonResponse.getJSONObject("user").getString("username")
                        var first = jsonResponse.getJSONObject("user").getJSONObject("profile").getString("firstname")
                        var last = jsonResponse.getJSONObject("user").getJSONObject("profile").getString("lastname")
                        mTvFullName.text = "$first $last"
                        mTvEmail.text = jsonResponse.getJSONObject("user").getJSONObject("profile").getString("email")
                        mTvAboutMe.text = jsonResponse.getJSONObject("user").getJSONObject("profile").getString("about")
                        if((currentUser.username != mTvUsername.text) || (currentUser.firstname != first) || (currentUser.lastname != last) ||
                            (currentUser.email != mTvEmail.text) || (currentUser.aboutMe != mTvAboutMe.text)){
                            val editor = mSharedPreferences!!.edit()
                            val gson = Gson()
                            var user = User()
                            user.username = mTvUsername.text as String?
                            user.firstname = first
                            user.lastname = last
                            user.email = mTvEmail.text as String?
                            user.aboutMe = mTvAboutMe.text as String?
                            editor.putString(Constants.CURRENT_USER, gson.toJson(user))
                            editor.apply()
                        }
                    } catch (e: Exception){
                        e.printStackTrace();
                    }
                } else {
                    handleError(mMessage)
                }
            }
        })
    }

    private fun showSnackBarMessage(message: String) {
        Snackbar.make(view!!.findViewById(R.id.activity_profile_llayout), message, Snackbar.LENGTH_SHORT).show()
    }
    private fun handleError(error: String) {
        //   val json = JSONObject(error)
        println(error)
        showSnackBarMessage("Error: " + error)
    }

    public fun getFullName():String?{
        return mTvFullName.text.toString()
    }
    public fun getUsername():String?{
        return mTvUsername.text.toString()
    }
    public fun getAboutMe():String?{
        return mTvAboutMe.text.toString()
    }
    fun getEmail():String?{
        return mTvEmail.text.toString()
    }



    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
