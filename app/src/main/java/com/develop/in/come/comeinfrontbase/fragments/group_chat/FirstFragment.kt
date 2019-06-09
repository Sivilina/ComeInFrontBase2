package com.develop.`in`.come.comeinfrontbase.fragments.group_chat
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.activities.GroupActivity
import com.develop.`in`.come.comeinfrontbase.activities.MainActivity
import com.develop.`in`.come.comeinfrontbase.activities.NewGroupActivity
import com.develop.`in`.come.comeinfrontbase.models.Room
import com.develop.`in`.come.comeinfrontbase.models.User
import com.develop.`in`.come.comeinfrontbase.util.Constants
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URISyntaxException
import java.util.ArrayList

@Suppress("DEPRECATED_IDENTITY_EQUALS", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class FirstFragment : Fragment(){
    lateinit var mlvGroupList: ListView
    lateinit var adapter: ArrayAdapter<*>
    lateinit var mSharedPreferences: SharedPreferences
    lateinit var mIbAddGroup: ImageButton
    lateinit var mSocket: Socket


    fun getGroupList() : ArrayList<String> {
        var res = ArrayList<String>()
        //res.add("CSSE 1503 Chat")
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val token = mSharedPreferences.getString(Constants.TOKEN,"")
        println("Debug: This is token $token")
        val client = OkHttpClient()


        val request = Request.Builder()
            .url(Constants.BASE_URL + "chats/rooms")
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

                val mMessage = response.body().string()
                println(mMessage)
                if (response.isSuccessful()){
                    try {
                        //  handleResponse(mMessage)
                        val jsonResponse = JSONObject(mMessage).getJSONObject("data")
                        val rooms = jsonResponse.getJSONArray("rooms")
                        val jsonSavedGroups = mSharedPreferences.getString(Constants.ROOMS,"")
                        var jsonArrayRoom = JSONArray()
                        for (i in 0..(rooms.length() - 1)) {
                            val room = rooms.getJSONObject(i)
                            val gson = Gson()
                            val newRoom = Room(room.getString("_id"),
                                room.getJSONObject("admin").getString("userId"),
                                room.getString("iconUrl"),
                                room.getInt("usersCount"),
                                room.getString("title"),
                                room.getString("description"),
                                room.getString("roomType"),
                                room.getString("createdAt"),
                                room.getString("updatedAt")
                            )
                            val jsonNewRoomToAdd = gson.toJson(newRoom)
                            try {
                                if (jsonSavedGroups.length !== 0) {
                                    jsonArrayRoom = JSONArray(jsonSavedGroups)
                                }
                                jsonArrayRoom.put(JSONObject(jsonNewRoomToAdd))
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                            res.add(room.getString("title"))
                        }
                        val editor = mSharedPreferences.edit()
                        editor.putString(Constants.ROOMS, jsonArrayRoom.toString())
                        editor.apply()
                    } catch (e: Exception){
                        e.printStackTrace();
                    }
                } else {
                    handleError(mMessage)
                }
            }
        })
        return res
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_groups, container, false)

        mlvGroupList = view.findViewById(R.id.lv_GroupList) as ListView
        var groupList  = getGroupList()

        adapter = ArrayAdapter<String>(this.context, android.R.layout.simple_selectable_list_item,groupList)

        mlvGroupList.setAdapter(adapter)
        mlvGroupList.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // Toast.makeText(this, "Click on " + listNotes[position].title, Toast.LENGTH_SHORT).show()
            val selectedItem = parent.getItemAtPosition(position) as String
            mSocket.emit("join",selectedItem)
            val intent = Intent(context, GroupActivity::class.java)
            intent.putExtra("groupName", selectedItem)
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            intent.putExtra("username", (mSharedPreferences.getString(Constants.NAME,"")))
            startActivity(intent)
        }

        mIbAddGroup = view.findViewById(R.id.ib_add_group) as ImageButton
        mIbAddGroup.setOnClickListener {
            val intent = Intent(context, NewGroupActivity::class.java)
            startActivity(intent)
        }
        return view
    }



    private fun handleError(error: String) {
        // val json = JSONObject(error)
        println(error)
        showSnackBarMessage("Error: " + error)
    }
    private fun showSnackBarMessage(message: String) {

        Snackbar.make(activity!!.findViewById(R.id.activity_main_llayout), message, Snackbar.LENGTH_SHORT).show()
    }
}