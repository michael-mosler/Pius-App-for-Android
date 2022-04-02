package com.rmkrings.interfaces;

import com.rmkrings.http.HttpResponseData;

/**
 * This interface must be implemented by call services that request data
 * from the backend.
 */
public interface HttpResponseCallback {
    /**
     * If data has been received from the backend it is passed to this method
     * for processing.
     * @param data Data received from backend
     */
    void execute(HttpResponseData data);

    /**
     * Whenever an internal error occured in loader this method is called for error
     * handling. E.g., for an interactive load the implemtation may display an error
     * message to he user. For backend loads nothing then writing a log message might
     * be a reasonable action on the other hand.
     * @param e Exception that caused this method to be called.
     */
    void onInternalError(Exception e);
}
