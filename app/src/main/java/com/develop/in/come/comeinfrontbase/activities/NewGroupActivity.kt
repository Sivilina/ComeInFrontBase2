package com.develop.`in`.come.comeinfrontbase.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import android.widget.Button
import androidx.appcompat.widget.SearchView
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.models.Room
import com.develop.`in`.come.comeinfrontbase.util.Constants
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import org.json.JSONArray
import java.util.*
import kotlin.collections.Map as Map1


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
    lateinit var mSv1: AutoCompleteTextView
    lateinit var mSv2: AutoCompleteTextView
    lateinit var mSv3: AutoCompleteTextView
    lateinit var mSv4: AutoCompleteTextView
    lateinit var mSv5: AutoCompleteTextView
    lateinit var mBtnAddTag: Button
    val languages = arrayOf( "C : 2","C++ : 56","Java : 85","C# : 78","PHP : 54","JavaScript : 47","jQuery : 56","AJAX : 70","JSON : 3")
    var mapTags = HashMap<String, String>()
    val MEDIA_TYPE = MediaType.parse("application/json")

    fun getTags(){
        val token = mSharedPreferences.getString(Constants.TOKEN,"")
        println("Debug: This is token $token")
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(Constants.BASE_URL + "chats/tags/all")
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

                val mMessage = response.body()!!.string()

                println(mMessage)
                if (response.isSuccessful()){
                    try {
                        //  handleResponse(mMessage)
                        val jsonResponse = JSONObject(mMessage).getJSONObject("data")
                        val tags = jsonResponse.getJSONArray("tags")
                        for(i in 0..tags.length()-1){
                            val tag = tags.getJSONObject(i)
                            println("Debug:"+tag)
                            // hashtagAdapter.add(Hashtag(tag.getString("name"), tag.getInt("usageCount")));
                            mapTags.put(tag.getString("_id"),tag.getString("name"))
                            languages.set(i,tag.getString("name") + " : " + tag.getInt("usageCount"))
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
        getTags()
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
        mEdGroupDescription = findViewById(R.id.et_groupdescription)
        mEdGroupName = findViewById(R.id.et_groupname)
        mSv1 = findViewById(R.id.actv1)
        mSv2 = findViewById(R.id.actv2)
        mSv3 = findViewById(R.id.actv3)
        mSv4 = findViewById(R.id.actv4)
        mSv5 = findViewById(R.id.actv5)
        var adapter = ArrayAdapter<String>(this,android.R.layout.select_dialog_singlechoice, languages);
                //Find TextView control
        //Set the number of characters the user must type before the drop down list is shown
        mSv1.setThreshold(1);
        mSv1.setAdapter(adapter);
        mSv2.setThreshold(1);
        mSv2.setAdapter(adapter);
        mSv3.setThreshold(1);
        mSv3.setAdapter(adapter);
        mSv4.setThreshold(1);
        mSv4.setAdapter(adapter);
        mSv5.setThreshold(1);
        mSv5.setAdapter(adapter);

    }

    fun searchTags(text: String){
        val token = mSharedPreferences.getString(Constants.TOKEN,"")
        println("Debug: This is token $token")
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(Constants.BASE_URL + "chats/tags/:" + text)
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

                val mMessage = response.body()!!.string()

                println(mMessage)
                if (response.isSuccessful()){
                    try {
                        //  handleResponse(mMessage)
                        val jsonResponse = JSONObject(mMessage).getJSONObject("data")
                        val tags = jsonResponse.getJSONArray("tags")
                        for(i in 0..tags.length()-1){
                            val tag = tags.getJSONObject(i)
                            println("Debug:"+tag)
                           // hashtagAdapter.add(Hashtag(tag.getString("name"), tag.getInt("usageCount")));
                            mapTags.put(tag.getString("_id"),tag.getString("name"))
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

        val tagJson = JSONArray()
        if(!mSv1.text.isEmpty()){
            var sv1 = JSONObject()
            if (mSv1.text.toString()!!.indexOf(":")!= -1)
                sv1.put("tagId",mapTags.get(mSv1.text.toString().substring(0,mSv1.text.toString().indexOf(":") - 1)))
            else
                sv1.put("tagId",mapTags.get(mSv1.text.toString()))
            sv1.put("name",mSv1.text)
            tagJson.put(sv1)}
        if(!mSv2.text.isEmpty()){
            var sv2 = JSONObject()
            if (mSv2.text.toString()!!.indexOf(":")!= -1)
                sv2.put("tagId",mapTags.get(mSv2.text.toString().substring(0,mSv2.text.toString().indexOf(":") - 1)))
            else
                sv2.put("tagId",mapTags.get(mSv2.text.toString()))
            sv2.put("name",mSv2.text)
            tagJson.put(sv2)}
        if(!mSv3.text.isEmpty()){
            var sv3 = JSONObject()

            if (mSv3.text.toString()!!.indexOf(":")!= -1)
                sv3.put("tagId",mapTags.get(mSv3.text.toString().substring(0,mSv3.text.toString().indexOf(":") - 1)))
            else
                sv3.put("tagId",mapTags.get(mSv3.text.toString()))
            sv3.put("name",mSv3.text)
            tagJson.put(sv3)}
        if(!mSv4.text.isEmpty()){
            var sv4 = JSONObject()

            if (mSv1.text.toString()!!.indexOf(":")!= -1)
                sv4.put("tagId",mapTags.get(mSv4.text.toString().substring(0,mSv4.text.toString().indexOf(":") - 1)))
            else
                sv4.put("tagId",mapTags.get(mSv1.text.toString()))
            sv4.put("name",mSv4.text)
            println(mapTags.get(mSv4.text.toString().substring(0,mSv4.text.toString().indexOf(":") - 1)))
            tagJson.put(sv4)}
        if(!mSv5.text.isEmpty()){
            var sv5 = JSONObject()

            if (mSv5.text.toString()!!.indexOf(":")!= -1)
                sv5.put("tagId",mapTags.get(mSv5.text.toString().substring(0,mSv5.text.toString().indexOf(":") - 1)))
            else
                sv5.put("tagId",mapTags.get(mSv1.text.toString()))
            println(mapTags.get(mSv5.text.toString().substring(0,mSv5.text.toString().indexOf(":") - 1)))
            sv5.put("name",mSv5.text)
            tagJson.put(sv5)}

        val postdata = JSONObject()
        try {
            postdata.put("roomType", "public")
            postdata.put("title", mEdGroupName.text)
            postdata.put("description", mEdGroupDescription.text)
            postdata.put("tags",tagJson)
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

                val mMessage = response.body()!!.string()
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
    @RequiresApi(Build.VERSION_CODES.GINGERBREAD)
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
        val intent = Intent(this, GroupActivity::class.java)
        intent.putExtra("roomid", newRoom.roomid)
        startActivity(intent)

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
