package com.develop.`in`.come.comeinfrontbase.fragments.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.models.Message
import com.develop.`in`.come.comeinfrontbase.models.MessageAdapter
import com.develop.`in`.come.comeinfrontbase.network.ChatApplication
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList


class MainFragment : Fragment() {

    lateinit var mMessagesView: RecyclerView
    lateinit var mInputMessageView: EditText
    var mMessages = ArrayList<Message>()
    lateinit var mAdapter: RecyclerView.Adapter<*>
    var mTyping = false
    val mTypingHandler = Handler()
    lateinit var mUsername: String
    lateinit var toId: String
    lateinit var mSocket: Socket


    private val isConnected = true

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

        // setHasOptionsMenu(true);

        val app = activity!!.application as ChatApplication
        mSocket = app.socket!!
        println(mSocket.connected())
        mSocket.on("getPrivateMessage", onNewMessage)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()

        mSocket.off("getPrivateMessage", onNewMessage)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMessagesView = view.findViewById<View>(R.id.messages) as RecyclerView
        mMessagesView.layoutManager = LinearLayoutManager(activity)
        mMessagesView.adapter = mAdapter

        mInputMessageView = view.findViewById<View>(R.id.message_input) as EditText
        mInputMessageView.setOnEditorActionListener(TextView.OnEditorActionListener { v, id, event ->
            if (id == 63 || id == EditorInfo.IME_NULL) {
                attemptSend()

                return@OnEditorActionListener true
            }
            false
        })
        val intent = activity!!.intent
        mUsername = intent.getStringExtra("username")

        toId = intent.getStringExtra("toId")
        val toName = intent.getStringExtra("toUser")
        //Debug: //println("$mUsername!!!!!!!$toId")
        addLog(toName)

        val sendButton = view.findViewById<View>(R.id.send_button) as ImageButton
        sendButton.setOnClickListener {
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
        if (!mSocket.connected()) return

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
        mSocket.emit("sendPrivateMessage", sentMessage)
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
