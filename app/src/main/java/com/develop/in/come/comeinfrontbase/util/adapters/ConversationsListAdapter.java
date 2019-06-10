package com.develop.in.come.comeinfrontbase.util.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;
import com.develop.in.come.comeinfrontbase.R;
import com.develop.in.come.comeinfrontbase.fragments.group_chat.listeners.OnConversationClickListener;
import com.develop.in.come.comeinfrontbase.fragments.group_chat.listeners.OnConversationLongClickListener;
import com.develop.in.come.comeinfrontbase.fragments.group_chat.listeners.OnSwipeMenuUnreadClickListener;
import com.develop.in.come.comeinfrontbase.models.Room;
import com.develop.in.come.comeinfrontbase.models.User;
import com.develop.in.come.comeinfrontbase.util.Constants;
import com.develop.in.come.comeinfrontbase.util.CropCircleTransformation;
import com.develop.in.come.comeinfrontbase.util.TimeUtils;
import com.google.gson.Gson;
import com.vanniktech.emoji.EmojiTextView;

import java.util.List;


public class ConversationsListAdapter extends AbstractRecyclerAdapter<Room,
        ConversationsListAdapter.ViewHolder> {
    private static final String TAG = ConversationsListAdapter.class.getName();

    private OnConversationClickListener onConversationClickListener;
    private OnConversationLongClickListener onConversationLongClickListener;
    private OnSwipeMenuUnreadClickListener onSwipeMenuUnreadClickListener;

    public User currentUser;

    SharedPreferences mSharedPreferences;
    public OnConversationClickListener getOnConversationClickListener() {
        return onConversationClickListener;
    }

    public void setOnConversationClickListener(OnConversationClickListener onConversationClickListener) {
        this.onConversationClickListener = onConversationClickListener;
    }

    public OnConversationLongClickListener getOnConversationLongClickListener() {
        return onConversationLongClickListener;
    }

    public void setOnConversationLongClickListener(
            OnConversationLongClickListener onConversationLongClickListener) {
        this.onConversationLongClickListener = onConversationLongClickListener;
    }

    public void setOnSwipeMenuUnreadClickListener(OnSwipeMenuUnreadClickListener onSwipeMenuUnreadClickListener) {
        this.onSwipeMenuUnreadClickListener = onSwipeMenuUnreadClickListener;
    }

    public OnSwipeMenuUnreadClickListener getOnSwipeMenuUnreadClickListener() {
        return onSwipeMenuUnreadClickListener;
    }

    public ConversationsListAdapter(Context context, List<Room> conversations) {
        super(context, conversations);
    }

    @Override
    public void setList(List<Room> mList) {
        super.setList(mList);
    }

    @Override
    public ConversationsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ConversationsListAdapter.ViewHolder holder, final int position) {
        final Room conversation = getItem(position);

        setRecipientPicture(holder, conversation);

        setRecipientDisplayName(holder, conversation.getTitle());

        setGroupSenderName(holder, conversation);

        setLastMessageText(holder, conversation);

        setTimestamp(holder, conversation.is_new(), conversation.getDateTime());

        setConversationCLickAction(holder, conversation, position);

        setConversationLongCLickAction(holder, conversation, position);


        setOnUnreadClickListener(holder, conversation, position);

        setTextButton(holder, conversation);
    }

    private void setRecipientPicture(ViewHolder holder, Room conversation) {
        String picture = conversation.getIconUrl();


        Gson gson = new Gson();
        String json = mSharedPreferences.getString(Constants.CURRENT_USER, "");
        currentUser = (User)gson.fromJson(json,User.class);
        Glide.with(holder.itemView.getContext())
                    .load(picture)
                    .placeholder(R.mipmap.ic_launcher_av_cat)
                    .bitmapTransform(new CropCircleTransformation(holder.itemView.getContext()))
                    .into(holder.recipientPicture);
    }

    // set the recipient display name whom are talking with
    private void setRecipientDisplayName(ViewHolder holder, String recipientFullName) {
       holder.recipientDisplayName.setText(recipientFullName);
    }

    // show te last message text
    private void setLastMessageText(ViewHolder holder, Room conversation) {

        // default text message
        String lastMessageText = conversation.getLast_message_text();

        if (conversation.is_new()) {
            // show bold text
            holder.lastTextMessage.setText(Html.fromHtml("<b>" + lastMessageText + "</b>"));
        } else {
            // not not bold text
            holder.lastTextMessage.setText(lastMessageText);
        }
    }

    private void setGroupSenderName(ViewHolder holder, Room conversation) {

        String sender = conversation.getSender().getUsername();

            if (sender != null) {
                holder.senderDisplayName.setText(sender + ": "); // set it
                holder.senderDisplayName.setVisibility(View.VISIBLE); // show it
            } else {
                holder.senderDisplayName.setVisibility(View.GONE); // hide it
            }
    }

    // show the last sent message timestamp
    private void setTimestamp(ViewHolder holder, boolean hasNewMessages, long timestamp) {
        // format the timestamp to a pretty visible format
        String formattedTimestamp = TimeUtils.getFormattedTimestamp(holder.itemView.getContext(), timestamp);

        if (hasNewMessages) {
            // show bold text
            holder.lastMessageTimestamp.setText(Html.fromHtml("<b>" + formattedTimestamp + "</b>"));
        } else {
            // not not bold text
            holder.lastMessageTimestamp.setText(formattedTimestamp);
        }
    }

    // set on row click listener
    private void setConversationCLickAction(ViewHolder holder,
                                            final Room conversation, final int position) {
        // use the swipe item click listener to solve the open/close issue
        // more details at https://github.com/daimajia/AndroidSwipeLayout/issues/403
        holder.swipeItem.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    getOnConversationClickListener().onConversationClicked(conversation, position);
            }
        });
    }

    // set on row long click listener
    private void setConversationLongCLickAction(ViewHolder holder,
                                                final Room conversation, final int position) {
        // use the swipe item click listener to solve the open/close issue
        // more details at https://github.com/daimajia/AndroidSwipeLayout/issues/403
        holder.swipeItem.getSurfaceView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (getOnConversationLongClickListener() != null) {
                    getOnConversationLongClickListener().onConversationLongClicked(conversation, position);

                    // source :
                    // https://stackoverflow.com/questions/18911290/perform-both-the-normal-click-and-long-click-at-button
                    return true; // event triggered
                } else {
                    Log.w(TAG, "ArchivedConversationsListAdapter.setConversationLongCLickAction:" +
                            " getOnConversationLongClickListener is null. " +
                            "set it with setOnConversationLongClickListener method. ");
                }

                return false; // event not triggered
            }
        });
    }


    private void setOnUnreadClickListener(final ViewHolder holder, final Room conversation, final int position) {
        holder.unread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // update the text button
                setTextButton(holder, conversation);

                getOnSwipeMenuUnreadClickListener().onSwipeMenuUnread(conversation, position);
            }
        });
    }

    /**
     * Dismiss the swipe menu for the view at position
     * @param position the position of the item to dismiss
     */
    public void dismissSwipeMenu(RecyclerView recyclerView, int position) {
        // retrieve the viewholder at position
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);

        // check if the viewholder is an instance of ConversationListAdapter.ViewHolder
        if(viewHolder instanceof ViewHolder) {

            // cast the holder to ConversationListAdapter.ViewHolder
            ViewHolder holder = (ViewHolder) viewHolder;

            // dismiss the menu
            holder.swipeItem.close();
        }
    }

    private void setTextButton(ViewHolder holder, Room conversation) {
        if(conversation.is_new()) {
            holder.unread.setText("Read");
        } else {
            holder.unread.setText("Unread");
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView recipientPicture;
        private TextView recipientDisplayName;
        private TextView senderDisplayName;
        private EmojiTextView lastTextMessage;
        private TextView lastMessageTimestamp;

        private TextView close;
        private TextView unread;
        private SwipeLayout swipeItem;

        public ViewHolder(View itemView) {
            super(itemView);

            recipientPicture = itemView.findViewById(R.id.recipient_picture);
            recipientDisplayName = itemView.findViewById(R.id.recipient_display_name);
            senderDisplayName = itemView.findViewById(R.id.sender_display_name);
            lastTextMessage = itemView.findViewById(R.id.last_text_message);
            lastMessageTimestamp = itemView.findViewById(R.id.last_message_timestamp);

            close = itemView.findViewById(R.id.close);
            unread = itemView.findViewById(R.id.unread);

            swipeItem = itemView.findViewById(R.id.swipe_item);
            swipeItem.setShowMode(SwipeLayout.ShowMode.PullOut);
//            swipeItem.addDrag(SwipeLayout.DragEdge.Left, swipeItem.findViewById(R.id.swipe_left));
            swipeItem.addDrag(SwipeLayout.DragEdge.Right, swipeItem.findViewById(R.id.swipe_right));
        }
    }
}