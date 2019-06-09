package com.develop.`in`.come.comeinfrontbase.activities

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.AppCompatButton
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.support.v7.widget.Toolbar
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.models.Room
import com.develop.`in`.come.comeinfrontbase.models.User
import com.develop.`in`.come.comeinfrontbase.util.Constants
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import org.json.JSONArray




@Suppress("DEPRECATED_IDENTITY_EQUALS", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class NewGroupActivity : AppCompatActivity() {
    lateinit var mToolbar: Toolbar
    lateinit var mTiGroupName: TextInputLayout
    lateinit var mEdGroupName: EditText
    lateinit var mTiGroupDescription: TextInputLayout
    lateinit var mEdGroupDescription: EditText
    lateinit var mTiFirstMessage: TextInputLayout
    lateinit var mEdFirstMessage: EditText
    lateinit var mSharedPreferences: SharedPreferences
    val MEDIA_TYPE = MediaType.parse("application/json")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_add_group)
        initviews()
    }

    fun initviews(){
        mToolbar = findViewById(R.id.toolbar_newGroup) as Toolbar
       mToolbar.setTitle("New group")
        mToolbar.setNavigationIcon(R.drawable.ic_back)
        setSupportActionBar(mToolbar)
        mToolbar.setNavigationOnClickListener(object : View.OnClickListener{
            override fun onClick(view: View) {
                finish()
            }
        })
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        /*  mToolbar.setOnMenuItemClickListener {
              val intent = Intent(this, GroupActivity::class.java)
              intent.putExtra("groupName", mEdGroupName.text)
              mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
              intent.putExtra("username", (mSharedPreferences.getString(Constants.NAME,"")))
              startActivity(intent)
              true
          }*/
        mTiGroupName = findViewById(R.id.ti_groupname)
        mTiGroupDescription = findViewById(R.id.ti_groupdescription)
        mTiFirstMessage = findViewById(R.id.ti_group_first_message)
        mEdFirstMessage = findViewById(R.id.et_group_first_message)
        mEdGroupDescription = findViewById(R.id.et_groupdescription)
        mEdGroupName = findViewById(R.id.et_groupname)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_camera,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == R.id.btn_save) {
            createGroup()
        }
        return super.onOptionsItemSelected(item)
    }

    fun createGroup(){
        val client = OkHttpClient()
        val postdata = JSONObject()
        try {
            postdata.put("roomType", "public")
            postdata.put("title", mEdGroupName.text)
            postdata.put("description", mEdGroupDescription.text)
         //   postdata.put("iconUrl", "")
        } catch (e: JSONException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        val body = RequestBody.create(
            MEDIA_TYPE,
            postdata.toString()
        )
        val token = mSharedPreferences.getString(Constants.TOKEN,"")
        println(token)
        val request = Request.Builder()
            .url(Constants.BASE_URL + "chats/rooms")
            .addHeader("Authorization", "Bearer $token")
            .post(body)
            .build()

        println(request.headers())
        println(request)
        println(postdata)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val mMessage = e.message.toString()
                println(mMessage)
                showSnackBarMessage(mMessage)
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
        val jsonSavedGroups = mSharedPreferences.getString(Constants.ROOMS,"")
        val jsonResponse = JSONObject(response).getJSONObject("data")
        val newRoom = Room(jsonResponse.getJSONObject("newRoom").getString("_id"),
            jsonResponse.getJSONObject("room").getJSONObject("admin").getString("userId"),
            jsonResponse.getJSONObject("room").getString("iconUrl"),
            jsonResponse.getJSONObject("room").getInt("usersCount"),
            jsonResponse.getJSONObject("room").getString("title"),
            jsonResponse.getJSONObject("room").getString("description"),
            jsonResponse.getJSONObject("room").getString("roomType"),
            jsonResponse.getJSONObject("room").getString("createdAt"),
            jsonResponse.getJSONObject("room").getString("updatedAt")
            )
        val jsonNewRoomToAdd = gson.toJson(newRoom)
        var jsonArrayRoom = JSONArray()
        try {
            if (jsonSavedGroups.length !== 0) {
                jsonArrayRoom = JSONArray(jsonSavedGroups)
            }
            jsonArrayRoom.put(JSONObject(jsonNewRoomToAdd))
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val editor = mSharedPreferences.edit()
        editor.putString(Constants.ROOMS, jsonArrayRoom.toString())
        editor.apply()
    }
    private fun handleError(error: String) {
       // val json = JSONObject(error)
        println(error)
        showSnackBarMessage("Error: " + error)
    }
    private fun showSnackBarMessage(message: String) {

        Snackbar.make(findViewById(R.id.layout_add_group), message, Snackbar.LENGTH_SHORT).show()
    }
}
