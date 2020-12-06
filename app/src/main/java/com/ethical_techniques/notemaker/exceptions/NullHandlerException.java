package com.ethical_techniques.notemaker.exceptions;

import android.util.Log;

public class NullHandlerException extends RuntimeException {

    public NullHandlerException(String message, String TAG_ID) {
        super(message);
        Log.e("NullHandlerException: " + TAG_ID, message, getCause());
        this.printStackTrace();

    }

}
