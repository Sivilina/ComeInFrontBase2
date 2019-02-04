package com.develop.`in`.come.comeinfrontbase.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.View
import android.support.v7.widget.Toolbar
import android.text.InputType
import android.widget.*
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.fragments.ProfileFragment
import com.develop.`in`.come.comeinfrontbase.fragments.SentFragment
import com.develop.`in`.come.comeinfrontbase.fragments.SettingsFragment
import com.develop.`in`.come.comeinfrontbase.fragments.TabFragment
import com.develop.`in`.come.comeinfrontbase.fragments.auth.ChangePasswordDialog
import com.develop.`in`.come.comeinfrontbase.models.User
import com.develop.`in`.come.comeinfrontbase.network.ChatApplication
import com.develop.`in`.come.comeinfrontbase.util.Constants

import rx.subscriptions.CompositeSubscription
import io.socket.client.Socket
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : AppCompatActivity(), ChangePasswordDialog.Listener {
    override fun onPasswordChanged() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    lateinit var mUserList: ArrayList<User>
    lateinit var mDrawerLayout: DrawerLayout
    lateinit var mNavigationView: NavigationView
    lateinit var mFragmentManager: FragmentManager
    lateinit var mFragmentTransaction: FragmentTransaction
    lateinit var mTvName: TextView
    lateinit var mIvAvatar: ImageView
    lateinit var mTvEmail: TextView
    lateinit var mToken: String
    lateinit var mEmail: String
    lateinit var mName: String
    lateinit var mSubscriptions: CompositeSubscription
    lateinit var mSharedPreferences: SharedPreferences
    lateinit var mSocket: Socket
    lateinit var mChApp: ChatApplication
    lateinit var mIbAddChat: ImageButton

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        initSharedPreferences()
        initSocket()
        loadUserList()
        loadNavView()
        //load()
    }
    fun initViews(){
        mDrawerLayout = findViewById<View>(R.id.drawerLayout) as DrawerLayout
        mNavigationView = findViewById<View>(R.id.navView) as NavigationView
        val header = mNavigationView.getHeaderView(0)
        mTvName = header.findViewById(R.id.tv_name) as TextView
        mTvEmail = header.findViewById(R.id.tv_email) as TextView
        mIvAvatar = header.findViewById(R.id.iv_avatar) as ImageView
        mIvAvatar.setOnClickListener{
            loadProfileLayout()
        }
        //mIbAddChat = header.findViewById(R.id.ib_addGroupChat) as ImageButton
        /*  mIbAddChat.setOnClickListener {
            addChat()
        }
        */
    }

    fun initSharedPreferences(){
      mSubscriptions = CompositeSubscription()
      mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
      mToken = mSharedPreferences.getString(Constants.TOKEN, "")
      mEmail = mSharedPreferences.getString(Constants.EMAIL, "")
      mName = mSharedPreferences.getString(Constants.NAME,"")
      mTvName.setText(mName)
      mTvEmail.setText(mEmail)
    }

    fun initSocket(){
      mChApp = getApplication() as ChatApplication
      mSocket = mChApp.socket!!
      mSocket.connect()
    }

    fun loadUserList(){
      mUserList = ArrayList<User>()
      val message = JSONObject()
      message.put("name",mName);
      message.put("email",mEmail)
      mSocket.emit("username",message);
      mSocket.on("userList") { args ->
          val obj = args[0] as JSONObject
          //Debug: //print("WORKING" + obj)
          try {
              val values = obj.getJSONArray("listUsers") as JSONArray
              //Debug: //println(values)
              var i = 0;
              mUserList.clear()
              for (i in 0..(values.length() - 1)) {
                  var item = values.getJSONObject(i) as JSONObject
                  var u = User()
                  u.name = item.getString("userName")
                  u.email = item.getString("email")
                  u.token = item.getString("id")
                  if(u.name != mName)
                      mUserList.add(u)
                          //Debug: //println(u)
              }
    //                adapter = ArrayAdapter<String>(this.context, android.R.layout.simple_selectable_list_item, getStringArrayFromModel(mUserList))
    //                mlvUserList.setAdapter(adapter)
          } catch (e: JSONException) {
              println("NOOOOOOOOOOOO" + e)
          }
      }
    }

    fun loadNavView(){
        mFragmentManager = supportFragmentManager
        mFragmentTransaction = mFragmentManager.beginTransaction()
        mFragmentTransaction.replace(R.id.containerView, TabFragment()).commit()

        mNavigationView.setNavigationItemSelectedListener {
                menuItem ->  mDrawerLayout.closeDrawers()

            if (menuItem.itemId == R.id.nav_item_groups){
                val ft = mFragmentManager.beginTransaction()
                ft.replace(R.id.containerView, TabFragment()).commit()
            }
            if (menuItem.itemId == R.id.nav_item_chats) {
                val ft = mFragmentManager.beginTransaction()
                ft.replace(R.id.containerView, SentFragment()).commit()
            }
            if (menuItem.itemId == R.id.nav_item_settings) {
                val ft = mFragmentManager.beginTransaction()
                ft.replace(R.id.containerView, SettingsFragment()).commit()
            }

            false
        }

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        val mDrawerToggle = ActionBarDrawerToggle(this,mDrawerLayout, toolbar,
            R.string.app_name,R.string.app_name)
        mDrawerLayout.setDrawerListener(mDrawerToggle)
        mDrawerToggle.syncState()
    }

    fun loadProfileLayout(){
        val ft = mFragmentManager.beginTransaction()
        mDrawerLayout.closeDrawers()
        ft.replace(R.id.containerView, ProfileFragment()).commit()
    }
/*
fun load(){
  mSubscriptions.add(NetworkUtil.getRetrofit(mToken).getProfile(mEmail).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe (
              this::handleResponse,this::handleError
          ))
}


private fun handleResponse(lUser: User) {
  user = User(lUser);
  mTvName.setText(user.getName())
  mTvEmail.setText(user.getEmail())
  mChApp = getApplication() as ChatApplication
  mSocket = mChApp.socket
  var sendMessageToSocket = mapOf(
          "email" to user.email,
          "name" to user.name)
  mSocket.emit(sendMessageToSocket.toString());
  println(sendMessageToSocket.toString())
}

private fun handleError(error: Throwable) {

  if (error is HttpException) {

      val gson = GsonBuilder().create()

      try {

          val errorBody = error.response().errorBody().string()
          val response = gson.fromJson<Any>(errorBody, Response::class.java) as Response
          showSnackBarMessage(response.getMessage().email)

      } catch (e: IOException) {
          e.printStackTrace()
      }

  } else {

      showSnackBarMessage("Network Error !")
  }
}
*/

    private fun showSnackBarMessage(message: String) {
      Snackbar.make(findViewById(R.id.activity_main_llayout), message, Snackbar.LENGTH_SHORT).show()

    }




    private fun addChat(){
      var builder = AlertDialog.Builder(this);
      var groupName:EditText?=null

      with(builder){
          setTitle("New group")
          setMessage("\n Name of the group:")
          groupName= EditText(context)
          groupName!!.hint="groupName"
          groupName!!.inputType = InputType.TYPE_CLASS_TEXT
          setPositiveButton("OK") {
              dialog, whichButton ->
              dialog.dismiss()
              var groupNme=groupName!!.text.toString()
            // result  textViewOutput.text=" Name: ${name} and Age:  ${age}"
          }
          setNegativeButton("Cancel") {
              dialog, whichButton ->
              //showMessage("Close the game or anything!")
              dialog.dismiss()
          }

      }
      val dialog = builder.create()
      dialog.setView(groupName)
      dialog.show()
    }


        public fun getUserList() : ArrayList<User>{
          return mUserList;
        }

        public fun getGroupList() :ArrayList<String>{
          var res = ArrayList<String>()
          res.add("CSSE 1503 Chat")
          return res
        }
        override fun onDestroy() {
          mSocket.off("userList")
          mSocket.disconnect()
          super.onDestroy()
        }

}