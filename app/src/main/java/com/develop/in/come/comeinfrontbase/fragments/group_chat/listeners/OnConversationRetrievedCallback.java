package com.develop.in.come.comeinfrontbase.fragments.group_chat.listeners;

import com.develop.in.come.comeinfrontbase.models.Room;
/**
 * Created by stefanodp91 on 19/10/17.
 */

public interface OnConversationRetrievedCallback {

    void onConversationRetrievedSuccess(Room conversation);

    void onNewConversationCreated(String conversationId);

    void onConversationRetrievedError(Exception e);
}
