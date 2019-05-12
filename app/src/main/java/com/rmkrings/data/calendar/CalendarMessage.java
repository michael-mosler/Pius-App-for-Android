package com.rmkrings.data.calendar;

import android.view.Gravity;

import java.io.Serializable;

public class CalendarMessage extends CalendarListItem implements Serializable {

    private final String messageText;
    private final int gravity;

    public CalendarMessage(String message) {
        this.messageText = message;
        this.gravity = Gravity.NO_GRAVITY;
    }

    public CalendarMessage(String message, int gravity) {
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
