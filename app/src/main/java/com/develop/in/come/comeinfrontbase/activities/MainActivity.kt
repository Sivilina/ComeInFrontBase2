package com.develop.`in`.come.comeinfrontbase.activities

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.View
import androidx.appcompat.widget.Toolbar
import android.widget.*
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.fragments.SentFragment
import com.develop.`in`.come.comeinfrontbase.fragments.settings.SettingsFragment
import com.develop.`in`.come.comeinfrontbase.fragments.TabFragment
import com.develop.`in`.come.comeinfrontbase.models.Room
import com.develop.`in`.come.comeinfrontbase.network.ChatApplication
import com.develop.`in`.come.comeinfrontbase.util.Constants
import com.develop.`in`.come.comeinfrontbase.util.GraphicsUtil
import com.google.gson.Gson

import rx.subscriptions.CompositeSubscription
import io.socket.client.Socket
import io.socket.emitter.Emitter
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.ArrayList
import com.develop.`in`.come.comeinfrontbase.models.User as User1

interface OnBackPressedListener {
    fun onBackPressed()
}
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : AppCompatActivity() {



    lateinit var mUserList: ArrayList<User1>
    lateinit var mDrawerLayout: androidx.drawerlayout.widget.DrawerLayout
    lateinit var mNavigationView: NavigationView
    lateinit var mFragmentManager: androidx.fragment.app.FragmentManager
    lateinit var mFragmentTransaction: androidx.fragment.app.FragmentTransaction
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
    lateinit var mCropImageUri: Uri
    lateinit var currentUser: User1
    private val NOTIFICATION_ID = 127
    var isConnected: Boolean? = true
    lateinit var mNotificationManager: NotificationManager
    val MEDIA_TYPE = MediaType.parse("application/json")





    private val REQUEST_CAMERA = 0
    val SELECT_FILE = 1
    val REQUEST_CROP_PICTURE = 2
    private val CROP_IMAGE_SIZE = 200
    private val CROP_IMAGE_HIGHLIGHT_COLOR = 0x6aa746F4
    lateinit var bitmapPic: Bitmap



    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mNotificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        initViews()
        initSharedPreferences()
        initSocket()
        //loadUserList()
        loadNavView()
        //load()
    }
    fun initViews(){
        mDrawerLayout = findViewById<View>(R.id.drawerLayout) as androidx.drawerlayout.widget.DrawerLayout
        mNavigationView = findViewById<View>(R.id.navView) as NavigationView
        val header = mNavigationView.getHeaderView(0)
        mTvName = header.findViewById(R.id.tv_name) as TextView
        mTvEmail = header.findViewById(R.id.tv_email) as TextView
        mIvAvatar = header.findViewById(R.id.iv_avatar) as ImageView
        mIvAvatar.setOnClickListener{
            loadProfileLayout()
        }
        mToken = "";
        mEmail = "";
        //mIbAddChat = header.findViewById(R.id.ib_addGroupChat) as ImageButton
        /*  mIbAddChat.setOnClickListener {
            addChat()
        }
        */


    }
    fun initSharedPreferences(){
        mSubscriptions = CompositeSubscription()
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val gson = Gson()
        val json = mSharedPreferences.getString(Constants.CURRENT_USER, "")
        currentUser = gson.fromJson<Any>(json, User1::class.java) as User1
        val graphicsUtil = GraphicsUtil()
        val avatar = graphicsUtil.decodeBase64(mSharedPreferences.getString("Avatar",""))
        if (avatar!= null)
            mIvAvatar.setImageBitmap(graphicsUtil.getRoundedShape(avatar,100,100))


      //  mToken = currentUser.token!!
      //  mEmail = currentUser.phone!!
        mTvEmail.setText(mEmail)
        mTvName.setText(currentUser.username)
    }


    fun initSocket(){
      mChApp = getApplication() as ChatApplication
      mSocket = mChApp.socket!!
      mSocket.connect()
        mSocket.on(Socket.EVENT_CONNECT, onConnect)
        mSocket.on("newMessage",onNewMessage)
    }

    private val onConnect = Emitter.Listener {
        runOnUiThread {
            if ((isConnected==false)) {
                renewal()
                Toast.makeText(
                    applicationContext,
                    R.string.connect, Toast.LENGTH_LONG
                ).show()
                isConnected = true
            }
        }
    }

    private val onNewMessage = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            val data = args[0] as JSONObject
            val message = data.getJSONObject("message")
            val attachments = message.getJSONArray("attachments")
            val deleted = message.getBoolean("deleted")
            val usersReadMessage = message.getJSONArray("messageRead")
            var prochitano = false
            for(i in 0..(usersReadMessage.length() - 1)){
                val user = usersReadMessage.getJSONObject(i)
                println("debug: $user")
                if(currentUser.userid!!.compareTo(user.toString()) == 0){
                    prochitano = true
                }
            }
            val mesId = message.getString("_id")
            val mesType = message.getString("messageType")
            val mesContent = message.getString("messageContent")
            val messageFrom = message.getJSONObject("messageFrom")
            val fromUserId = messageFrom.getString("userId")
            val fromUser = getUserByUserId(fromUserId)
            val roomId = message.getString("messageToRoom")
            var createdAt = message.getString("createdAt")
            var updatedAt = message.getString("updatedAt")

            notifyMessage(fromUser!!.username!!,mesContent)//временная проверка

//                val roomMessagesArray = message.getJSONArray("messages")
//                for (j in 0..(roomMessagesArray.length() - 1)) {
//                    val roomMessage = roomMessagesArray.getJSONObject(j)
//                    val messageDateTime = roomMessage.getString("messageDateTime")
//                }

        })
    }
    fun renewal(){
        val data = JSONObject()
        val jsonSavedGroups = mSharedPreferences.getString(Constants.ROOMS,"")
        var jsonArrayRoom = JSONArray(jsonSavedGroups)
        var tempJO = JSONObject()
        for (i in 0..(jsonArrayRoom.length() - 1)) {
            val room = jsonArrayRoom.getJSONObject(i) as Room
            tempJO.put("roomId",room.roomid)
            tempJO.put("messageDateTime",room.lastMessage)
        }
        try{
            data.put("data",tempJO)
        }catch (e: JSONException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        mSocket.emit("renewal",data)
    }

    private fun notifyMessage(username: String, message: String){
        var builder = Notification.Builder(applicationContext)
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("fullname", "ZorRo")
        var pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        builder
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setLargeIcon(BitmapFactory.decodeResource(applicationContext.resources,R.mipmap.ic_launcher_foreground))
            .setTicker(username+": "+message)
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(true)
            .setContentTitle(username)
            .setContentText(message)

        var notification = builder.build()
        mNotificationManager.notify(NOTIFICATION_ID,notification)
    }

    fun loadUserList(){
      mUserList = ArrayList<User1>()
      val message = JSONObject()
     // message.put("name",mName);
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
                  var u = User1()
                  u.username = item.getString("userName")
                  u.email = item.getString("email")
                  u.token = item.getString("id")
                  if(u.username != mName)
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
                ft.replace(R.id.containerView,
                    SettingsFragment()
                ).commit()
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
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
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




//    private fun addChat(){
//      var builder = AlertDialog.Builder(this);
//      var groupName:EditText?=null
//
//      with(builder){
//          setTitle("New group")
//          setMessage("\n Name of the group:")
//          groupName= EditText(context)
//          groupName!!.hint="groupName"
//          groupName!!.inputType = InputType.TYPE_CLASS_TEXT
//          setPositiveButton("OK") {
//              dialog, whichButton ->
//              dialog.dismiss()
//              var groupNme=groupName!!.text.toString()
//            // result  textViewOutput.text=" Name: ${name} and Age:  ${age}"
//          }
//          setNegativeButton("Cancel") {
//              dialog, whichButton ->
//              //showMessage("Close the game or anything!")
//              dialog.dismiss()
//          }
//
//      }
//      val dialog = builder.create()
//      dialog.setView(groupName)
//      dialog.show()
//    }


       /* public fun getUserList() : ArrayList<User>{
          return mUserList;
        }*/


        override fun onDestroy() {
            mSocket.off(Socket.EVENT_CONNECT, onConnect)
            mSocket.off("newMessage",onNewMessage)
            mSocket.disconnect()
            super.onDestroy()
        }

    override fun onBackPressed() {

        var backPressedListener: OnBackPressedListener?=null
        for (fragment in mFragmentManager.getFragments()) {
            if (fragment is  OnBackPressedListener) {
                backPressedListener = fragment as OnBackPressedListener;
                break;
            }
        }

        if (backPressedListener != null) {
             backPressedListener.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    override fun onResume() {
        super.onResume()
        val graphicsUtil = GraphicsUtil()
        val avatar = graphicsUtil.decodeBase64(mSharedPreferences.getString("Avatar",""))
        if (avatar!= null)
            mIvAvatar.setImageBitmap(graphicsUtil.getRoundedShape(avatar,100,100))

    }

    fun getUserByUserId(userId:String): com.develop.`in`.come.comeinfrontbase.models.User? {
        val token = mSharedPreferences.getString(Constants.TOKEN,"")
        println("Debug: This is token $token")
        val client = OkHttpClient()

        var user = null as com.develop.`in`.come.comeinfrontbase.models.User
        val request = Request.Builder()
            .url(Constants.BASE_URL + "users/profile:" + userId)
            .addHeader("Authorization", "bearer $token")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val mMessage = e.message.toString()
                println(mMessage)
                handleError(mMessage);
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: okhttp3.Response) {

                user = com.develop.`in`.come.comeinfrontbase.models.User()
                val mMessage = response.body()!!.string()
                println(mMessage)
                if (response.isSuccessful()){
                    try {
                        //  handleResponse(mMessage)
                        val jsonResponse = JSONObject(mMessage).getJSONObject("data")
                        //mTvUsername.text = jsonResponse.getJSONObject("user").getString("username")
                        user.firstname = jsonResponse.getJSONObject("user").getJSONObject("profile").getString("firstname")
                        user.lastname = jsonResponse.getJSONObject("user").getJSONObject("profile").getString("lastname")
                        user.email = jsonResponse.getJSONObject("user").getJSONObject("profile").getString("email")
                        user.aboutMe = jsonResponse.getJSONObject("user").getJSONObject("profile").getString("about")
                        user.userid = userId
                        user.username = jsonResponse.getJSONObject("user").getString("username")

                    } catch (e: Exception){
                        e.printStackTrace();
                    }
                } else {
                    handleError(mMessage)
                }
            }
        })
        return user
    }
    private fun handleError(error: String) {
        //   val json = JSONObject(error)
        println(error)
        showSnackBarMessage("Error: " + error)
    }

}
