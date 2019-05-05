package com.rmkrings.data.calendar;

import java.io.Serializable;

public class CalendarMessage extends CalendarListItem implements Serializable {

    private String messageText;

    public CalendarMessage(String message) {
        this.messageText = message;
    }

    public String getMessageText() {
        return messageText;
    }

    @Override
    public int getType() {
        return message;
    }
}
