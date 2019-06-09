package com.develop.`in`.come.comeinfrontbase.models

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.develop.`in`.come.comeinfrontbase.R
import com.develop.`in`.come.comeinfrontbase.models.Message.Companion
import java.util.ArrayList


class MessageAdapter(context: Context, private val mMessages: ArrayList<com.develop.`in`.come.comeinfrontbase.models.Message>) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    private val mUsernameColors: IntArray

    init {
        mUsernameColors = context.resources.getIntArray(R.array.username_colors)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var layout = -1
        when (viewType) {
            Message.TYPE_MESSAGE -> layout = R.layout.item_message
            Message.TYPE_LOG -> layout = R.layout.item_log
            Message.TYPE_ACTION -> layout = R.layout.item_action
        }
        val v = LayoutInflater
            .from(parent.context)
            .inflate(layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val message:Message = mMessages[position]
        viewHolder.setMessage(message.mMessage)
        viewHolder.setUsername(message.mUsername)
    }

    override fun getItemCount(): Int {
        return mMessages.size
    }

    override fun getItemViewType(position: Int): Int {
        return mMessages[position].mType
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mUsernameView: TextView? = itemView.findViewById(R.id.username) as? TextView
        private val mMessageView: TextView? = itemView.findViewById(R.id.message) as? TextView

        fun setUsername(username: String?) {
            if (mUsernameView != null) {
                mUsernameView.text = username
                mUsernameView.setTextColor(getUsernameColor(username!!))
            }

        }

        fun setMessage(message: String?) {
            if (mMessageView != null) {
                mMessageView.text = message
            }
        }

        private fun getUsernameColor(username: String): Int {
            var hash = 7
            var i = 0
            val len = username.length
            while (i < len) {
                hash = username.codePointAt(i) + (hash shl 5) - hash
                i++
            }
            val index = Math.abs(hash % mUsernameColors.size)
            return mUsernameColors[index]
        }
    }
}
