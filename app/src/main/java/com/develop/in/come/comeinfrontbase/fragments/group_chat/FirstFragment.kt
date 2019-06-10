package com.develop.`in`.come.comeinfrontbase.fragments.group_chat
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.activities.MainActivity
import com.develop.`in`.come.comeinfrontbase.activities.NewGroupActivity
import com.develop.`in`.come.comeinfrontbase.fragments.group_chat.listeners.*
import com.develop.`in`.come.comeinfrontbase.models.Room
import com.develop.`in`.come.comeinfrontbase.models.User
import com.develop.`in`.come.comeinfrontbase.util.Constants
import com.develop.`in`.come.comeinfrontbase.util.ItemDecoration
import com.develop.`in`.come.comeinfrontbase.util.adapters.ConversationsListAdapter
import com.google.gson.Gson
import io.socket.client.Socket
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.ArrayList

@Suppress("DEPRECATED_IDENTITY_EQUALS", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class FirstFragment : androidx.fragment.app.Fragment(), ConversationsListener,
    OnConversationClickListener,
    OnConversationLongClickListener,
    OnSwipeMenuUnreadClickListener {
    override fun onConversationAdded(conversation: Room?) {
        conversationsListAdapter.notifyDataSetChanged();
    }

    override fun onConversationChanged(conversation: Room?) {
        conversationsListAdapter.notifyDataSetChanged();
    }

    override fun onConversationRemoved() {
        conversationsListAdapter.notifyDataSetChanged();
    }

    override fun onConversationClicked(conversation: Room?, position: Int) {
//        conversationsHandler.setConversationRead(conversation.getConversationId());
    }

    override fun onConversationLongClicked(conversation: Room?, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }




    override fun onSwipeMenuUnread(conversation: Room?, position: Int) {
        // dismiss the swipe menu
        conversationsListAdapter.dismissSwipeMenu(recyclerViewConversations, position);
        conversationsListAdapter.notifyItemChanged(position);
    }

    //  lateinit var adapter: ArrayAdapter<*>
    lateinit var mSharedPreferences: SharedPreferences
    lateinit var mIbAddGroup: ImageButton
    lateinit var mSocket: Socket

    lateinit var recyclerViewConversations: androidx.recyclerview.widget.RecyclerView;
    lateinit var rvConversationsLayoutManager: androidx.recyclerview.widget.LinearLayoutManager;
    lateinit var conversationsListAdapter: ConversationsListAdapter;
    lateinit var addNewConversation: FloatingActionButton;
    lateinit var subheader:LinearLayout;
    lateinit var currentUserGroups:TextView;

fun getGroupList() :ArrayList<Room>{
    var res = ArrayList<Room>()
    mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val jsonSavedGroups = mSharedPreferences.getString(Constants.ROOMS,"")
if(jsonSavedGroups.length>0){
    val json = JSONObject(jsonSavedGroups)
    val jsonArrayGroup = json.getJSONArray(Constants.ROOMS)
    println("JSONSAVED" + jsonSavedGroups)
    println("JSONArr" + jsonArrayGroup)

    for(k in 0 until jsonArrayGroup.length()){
        var group = jsonArrayGroup.getJSONObject(k).toString()
        val gson = Gson()
        val newRoom = gson.fromJson<Any>(group, Room::class.java) as Room
        res.add(newRoom)
    }
   }
    return res;
}

    fun getGroupListFromServer() : ArrayList<Room> {
        var res = ArrayList<Room>()
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

                val mMessage = response.body()!!.string()
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
                            res.add(newRoom)
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

    fun newInstance(): androidx.fragment.app.Fragment {
        var mFragment = FirstFragment();
        return mFragment;
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_groups, container, false)
        recyclerViewConversations = view.findViewById(R.id.conversations_list);
        recyclerViewConversations.addItemDecoration(ItemDecoration(getContext(),
                androidx.recyclerview.widget.DividerItemDecoration.VERTICAL,
                getResources().getDrawable(R.drawable.decorator_fragment_conversation_list)));
        rvConversationsLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(getActivity());
        recyclerViewConversations.setLayoutManager(rvConversationsLayoutManager);

         // init RecyclerView adapter
        conversationsListAdapter = ConversationsListAdapter(
                getActivity(), getGroupList());
        conversationsListAdapter.setOnConversationClickListener(this);
        conversationsListAdapter.setOnConversationLongClickListener(this);
        conversationsListAdapter.setOnSwipeMenuUnreadClickListener(this);
        recyclerViewConversations.setAdapter(conversationsListAdapter);

        addNewConversation = view!!.findViewById(R.id.button_new_conversation) as FloatingActionButton;
        setAddNewConversationClickBehaviour();

        currentUserGroups = view.findViewById(R.id.groups);
        subheader = view.findViewById(R.id.subheader);
        showCurrentUserGroups();
     //   adapter = ArrayAdapter<String>(this.context, android.R.layout.simple_selectable_list_item,groupList)

//        mlvGroupList.setAdapter(adapter)
//        mlvGroupList.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
//            // Toast.makeText(this, "Click on " + listNotes[position].title, Toast.LENGTH_SHORT).show()
//            val selectedItem = parent.getItemAtPosition(position) as String
//            mSocket.emit("join",selectedItem)
//            val intent = Intent(context, GroupActivity::class.java)
//            intent.putExtra("groupName", selectedItem)
//            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
//            intent.putExtra("username", (mSharedPreferences.getString(Constants.NAME,"")))
//            startActivity(intent)
//        }

//        mIbAddGroup = view.findViewById(R.id.ib_add_group) as ImageButton
//        mIbAddGroup.setOnClickListener {
//            val intent = Intent(context, NewGroupActivity::class.java)
//            startActivity(intent)
//        }
        return view
    }

    fun setAddNewConversationClickBehaviour() {

        addNewConversation.setOnClickListener{
            val intent = Intent(context, NewGroupActivity::class.java)
            startActivity(intent)
          //  activity!!.finish()
        }
    }

    fun showCurrentUserGroups() {
            // groups enabled
            currentUserGroups.setOnClickListener{
//                val intent =  Intent(getActivity(), )
//                startActivity(intent)
                }
            subheader.setVisibility(View.VISIBLE)
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