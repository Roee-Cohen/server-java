package com.utils;

public enum Status{
    OK(201),
    UNAUTHORIZED(401),
    BAD_REQUEST(402),
    FORBIDDEN(403),
    NOTFOUND(404),
    INTERNAL_SERVER_ERROR(500);

    private int status;

    Status(int s){
        this.status = s;
    }

    public int getStatus() {
        return status;
    }
}
