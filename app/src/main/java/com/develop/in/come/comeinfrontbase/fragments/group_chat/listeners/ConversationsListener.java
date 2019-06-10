package com.develop.in.come.comeinfrontbase.fragments.group_chat.listeners;

import com.develop.in.come.comeinfrontbase.models.Room;

/**
 * Created by andrealeo on 06/12/17.
 */

public interface ConversationsListener {

    public void onConversationAdded(Room conversation);

    public void onConversationChanged(Room conversation);

    public void onConversationRemoved();

}

