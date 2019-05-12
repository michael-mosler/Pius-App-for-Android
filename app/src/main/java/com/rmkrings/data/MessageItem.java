package com.rmkrings.data;

import java.io.Serializable;

public class MessageItem extends BaseListItem implements Serializable {

    private String messageText;
    private int gravity;

    protected MessageItem() { }

    /*
    public MessageItem(String message) {
        this.messageText = message;
        this.gravity = Gravity.NO_GRAVITY;
    }
    */

    public MessageItem(String message, int gravity) {
        this.messageText = message;
        this.gravity = gravity;
    }

    public String getMessageText() {
        return messageText;
    }

    public int getGravity() {
        return gravity;
    }

    @Override
    public int getType() {
        return message;
    }
}
