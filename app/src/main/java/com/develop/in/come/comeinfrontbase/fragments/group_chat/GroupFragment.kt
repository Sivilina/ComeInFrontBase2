package com.develop.`in`.come.comeinfrontbase.fragments.group_chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.models.Message
import com.develop.`in`.come.comeinfrontbase.models.MessageAdapter
import com.develop.`in`.come.comeinfrontbase.models.Room
import com.develop.`in`.come.comeinfrontbase.models.User
import com.develop.`in`.come.comeinfrontbase.util.Constants
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URISyntaxException
import java.util.ArrayList


class GroupFragment : androidx.fragment.app.Fragment() {
    lateinit var mSocket: Socket

    lateinit var mMessagesView: androidx.recyclerview.widget.RecyclerView
    lateinit var mInputMessageView: EditText
    val mMessages = ArrayList<Message>()
    lateinit var mAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>
    var mTyping = false
    val mTypingHandler = Handler()
    lateinit var mUsername: String
    lateinit var mGroupName: String
    var isConnected: Boolean? = true
    var showOnlyTop = true
    lateinit  var room : Room
    lateinit var mSharedPreferences: SharedPreferences
    lateinit var currentUser:User

    private val onConnect = Emitter.Listener {
        activity!!.runOnUiThread {
            if ((isConnected==false)) {
                mSocket.emit("add user", mUsername)
                Toast.makeText(
                    activity!!.applicationContext,
                    R.string.connect, Toast.LENGTH_LONG
                ).show()
                isConnected = true
            }
        }
    }

    private val onDisconnect = Emitter.Listener {
        activity!!.runOnUiThread {
            Log.i(TAG, "diconnected")
            isConnected = false
            Toast.makeText(
                activity!!.applicationContext,
                R.string.disconnect, Toast.LENGTH_LONG
            ).show()
        }
    }

    private val onConnectError = Emitter.Listener {
        activity!!.runOnUiThread {
            Log.e(TAG, "Error connecting")
            Toast.makeText(
                activity!!.applicationContext,
                R.string.error_connect, Toast.LENGTH_LONG
            ).show()
        }
    }

    private val onNewMessage = Emitter.Listener { args ->
        activity!!.runOnUiThread(Runnable {
            val data = args[0] as JSONObject
            val username: String
            val message: String
            try {
                username = data.getString("username")
                message = data.getString("message")
                println("$username@@$4535$message")
            } catch (e: JSONException) {
                Log.e(TAG, e.message)
                return@Runnable
            }
            removeTyping(username)
            addMessage(username, message)
        })
    }

    private val onUserJoined = Emitter.Listener { args ->
        activity!!.runOnUiThread(Runnable {
            val data = args[0] as JSONObject
            val username: String
            val numUsers: Int
            try {
                username = data.getString("username")
                numUsers = data.getInt("numUsers")
            } catch (e: JSONException) {
                Log.e(TAG, e.message)
                return@Runnable
            }

            addLog(resources.getString(R.string.message_user_joined, username))
            addParticipantsLog(numUsers)
        })
    }

    private val onUserLeft = Emitter.Listener { args ->
        activity!!.runOnUiThread(Runnable {
            val data = args[0] as JSONObject
            val username: String
            val numUsers: Int
            try {
                username = data.getString("username")
                numUsers = data.getInt("numUsers")
            } catch (e: JSONException) {
                Log.e(TAG, e.message)
                return@Runnable
            }

            addLog(resources.getString(R.string.message_user_left, username))
            addParticipantsLog(numUsers)
            removeTyping(username)
        })
    }

    private val onTyping = Emitter.Listener { args ->
        activity!!.runOnUiThread(Runnable {
            val data = args[0] as JSONObject
            val username: String
            try {
                username = data.getString("username")
            } catch (e: JSONException) {
                Log.e(TAG, e.message)
                return@Runnable
            }

            addTyping(username)
        })
    }

    private val onStopTyping = Emitter.Listener { args ->
        activity!!.runOnUiThread(Runnable {
            val data = args[0] as JSONObject
            val username: String
            try {
                username = data.getString("username")
            } catch (e: JSONException) {
                Log.e(TAG, e.message)
                return@Runnable
            }

            removeTyping(username)
        })
    }

    private val onTypingTimeout = Runnable {
        if (!mTyping) return@Runnable

        mTyping = false
        mSocket.emit("stop typing")
    }

    init {
        try {
            val opts = IO.Options()
            opts.forceNew = true

            mSocket = IO.socket(Constants.GROUP_SERVER_URL, opts)
            println("Socket works!")
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }

    }


    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mAdapter = MessageAdapter(context!!, mMessages)
        if (context is Activity) {
            //this.listener = (MainActivity) context;
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
//        mSocket.connect()
//        mSocket.on(Socket.EVENT_CONNECT, onConnect)
//        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect)
//        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
//        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)
//        mSocket.on("new message", onNewMessage)
//        mSocket.on("user joined", onUserJoined)
//        mSocket.on("user left", onUserLeft)
//        mSocket.on("typing", onTyping)
//        mSocket.on("stop typing", onStopTyping)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()

//        mSocket.disconnect()
//        mSocket.off(Socket.EVENT_CONNECT, onConnect)
//        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect)
//        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError)
//        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)
//        mSocket.off("new message", onNewMessage)
//        mSocket.off("user joined", onUserJoined)
//        mSocket.off("user left", onUserLeft)
//        mSocket.off("typing", onTyping)
//        mSocket.off("stop typing", onStopTyping)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMessagesView = view.findViewById(R.id.messages) as androidx.recyclerview.widget.RecyclerView
        mMessagesView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        mMessagesView.adapter = mAdapter

        mInputMessageView = view.findViewById(R.id.message_input) as EditText
        mInputMessageView.setOnEditorActionListener(TextView.OnEditorActionListener { v, id, event ->
            if (id == 63 || id == EditorInfo.IME_NULL) {
                attemptSend()
                return@OnEditorActionListener true
            }
            false
        })
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity!!.applicationContext)
        val data = activity!!.intent
        val gson = Gson()
        val roomId = data.getStringExtra("roomid");
        val jsonSavedGroups = mSharedPreferences.getString(Constants.ROOMS,"")
        var jsonArrayRoom = JSONArray(jsonSavedGroups)
        for(i in 0..jsonArrayRoom.length() - 1)
        {
            val r = jsonArrayRoom.getJSONObject(i).toString()
            val roomTemp = gson.fromJson<Any>(r, Room::class.java) as Room
            if(roomTemp.roomid == roomId) {
                room = roomTemp;
                break;
            }
        }

        val json = mSharedPreferences.getString(Constants.CURRENT_USER, "")
        currentUser = gson.fromJson<Any>(json, User::class.java) as User

        mUsername = currentUser.username!!
        mGroupName = room.title!!
        println("!!!" + mUsername)
        addLog(mGroupName)
        mInputMessageView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!mSocket.connected()) return

                if (!mTyping) {
                    mTyping = true
                    mSocket.emit("typing")
                }

                mTypingHandler.removeCallbacks(onTypingTimeout)
                mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH.toLong())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        val sendButton = view.findViewById(R.id.ib_send_message) as ImageButton
        sendButton.setOnClickListener { attemptSend() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Activity.RESULT_OK != resultCode) {
            activity!!.finish()
            return
        }
    }


    private fun addLog(message: String?) {
        mMessages.add(
            Message.Builder(Message.TYPE_LOG)
                .message(message!!).build()
        )
        mAdapter.notifyItemInserted(mMessages.size - 1)
        scrollToBottom()
    }

    private fun addParticipantsLog(numUsers: Int) {
        addLog(resources.getQuantityString(R.plurals.message_participants, numUsers, numUsers))
    }

    private fun addMessage(username: String, message: String) {
        mMessages.add(
            Message.Builder(Message.TYPE_MESSAGE)
                .username(username).message(message).build()
        )
        mAdapter.notifyItemInserted(mMessages.size - 1)
        scrollToBottom()
    }

    private fun addTyping(username: String) {
        mMessages.add(
            Message.Builder(Message.TYPE_ACTION)
                .username(username).build()
        )
        mAdapter.notifyItemInserted(mMessages.size - 1)
        scrollToBottom()
    }

    private fun removeTyping(username: String) {
        for (i in mMessages.indices.reversed()) {
            val message = mMessages[i]
            if (message.mType == Message.TYPE_ACTION && message.mUsername.equals(username)) {
                mMessages.removeAt(i)
                mAdapter.notifyItemRemoved(i)
            }
        }
    }

    private fun attemptSend() {
        if (!mSocket.connected()) return
        mTyping = false
        val message = mInputMessageView.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(message)) {
            mInputMessageView.requestFocus()
            return
        }
        mInputMessageView.setText("")
        addMessage(mUsername, message)

        // perform the sending message attempt.
        mSocket.emit("new message", message)
    }



    private fun scrollToBottom() {
        mMessagesView.scrollToPosition(mAdapter.itemCount - 1)
    }

    companion object {
        private val TAG = "GroupFragment"

        private val REQUEST_LOGIN = 0

        private val TYPING_TIMER_LENGTH = 600
    }
}

