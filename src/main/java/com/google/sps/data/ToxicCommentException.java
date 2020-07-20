package com.google.sps.data;

import java.lang.Exception; 

public class ToxicCommentException extends Exception { 
    public ToxicCommentException(String errorMessage) {
        super(errorMessage);
    }
}
