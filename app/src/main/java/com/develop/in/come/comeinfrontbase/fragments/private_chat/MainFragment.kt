package com.develop.`in`.come.comeinfrontbase.fragments.private_chat

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.activities.ChatActivity
import com.develop.`in`.come.comeinfrontbase.models.Message
import com.develop.`in`.come.comeinfrontbase.models.MessageAdapter
import com.develop.`in`.come.comeinfrontbase.models.User
import com.develop.`in`.come.comeinfrontbase.util.Constants
import com.google.gson.Gson
import io.socket.client.Socket
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList


class MainFragment : androidx.fragment.app.Fragment() {

    lateinit var mMessagesView: androidx.recyclerview.widget.RecyclerView
    lateinit var mInputMessageView: EditText
    var mMessages = ArrayList<Message>()
    lateinit var mAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>
    var mTyping = false
    val mTypingHandler = Handler()
    lateinit var mUsername: String
    lateinit var toId: String
    lateinit var mSocket: Socket
    lateinit var mBtnSend: ImageButton
    lateinit var mSharedPreferences: SharedPreferences

    private val isConnected = true
    private val NOTIFICATION_ID = 127
/*
    private val onNewMessage = Emitter.Listener { args ->
        activity!!.runOnUiThread(Runnable {
            val data = args[0] as JSONObject
            val username: String
            val message: String
            try {
                username = data.getString("name")
                message = data.getString("text")
                //Debug: println("WORK!!!!$username ^ $message")
            } catch (e: JSONException) {
                Log.e(TAG, e.message)
                //Debug: println("MFFMFMF" + e.message)
                return@Runnable
            }

            addMessage(username, message)
        })
    }

*/
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
        initSharedPreferences()
        setHasOptionsMenu(true);
           /*  val app = activity!!.application as ChatApplication
        mSocket = app.socket!!
        println(mSocket.connected())
        mSocket.on("getPrivateMessage", onNewMessage)*/

    }

    private fun initSharedPreferences() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()

     //   mSocket.off("getPrivateMessage", onNewMessage)

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

        mBtnSend = view.findViewById(R.id.ib_send_message) as ImageButton
        val gson = Gson()
        val json = mSharedPreferences.getString(Constants.CURRENT_USER, "")
        val currentUser = gson.fromJson<Any>(json, User::class.java) as User

        val intent = activity!!.intent
        //mUsername = intent.getStringExtra("username")
        mUsername = currentUser.username!!
        toId = "1"
        //toId = intent.getStringExtra("toId")
       // val toName = intent.getStringExtra("toUser")
        //Debug: //println("$mUsername!!!!!!!$toId")
       // addLog(toName)

        mBtnSend.setOnClickListener {
            attemptSend()
            //Debug: //println("try to send!!!!!")
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Activity.RESULT_OK != resultCode) {
            activity!!.finish()
            return
        }
    }


    private fun addMessage(username: String, message: String) {
        mMessages.add(
            Message.Builder(Message.TYPE_MESSAGE)
                .username(username).message(message).build()
        )
        mAdapter.notifyItemInserted(mMessages.size - 1)
        scrollToBottom()
    }


    private fun attemptSend() {
        //if (!mSocket.connected()) return

        mTyping = false
        val message = mInputMessageView.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(message)) {
            mInputMessageView.requestFocus()
            return
        }

        mInputMessageView.setText("")
        val sentMessage = JSONObject()
        println(toId)
        try {
            sentMessage.put("toid", toId)
            sentMessage.put("name", mUsername)
            sentMessage.put("message", message)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        addMessage(mUsername, message)
        println(sentMessage)
        // perform the sending message attempt.
        //mSocket.emit("sendPrivateMessage", sentMessage)
    }


    private fun scrollToBottom() {
        mMessagesView.scrollToPosition(mAdapter.itemCount - 1)
    }

    private fun addLog(message: String) {
        mMessages.add(
            Message.Builder(Message.TYPE_LOG)
                .message(message).build()
        )
        mAdapter.notifyItemInserted(mMessages.size - 1)
        scrollToBottom()
    }



    companion object {

        private val TAG = "MainFragment"

        private val REQUEST_LOGIN = 0

        private val TYPING_TIMER_LENGTH = 600
    }


}
