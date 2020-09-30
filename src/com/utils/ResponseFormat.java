package com.utils;

import com.utils.Status;

public class ResponseFormat {

    public Status status;
    public String data;

    public ResponseFormat(Status status, String data){
        this.status = status;
        this.data = data;
    }
}
