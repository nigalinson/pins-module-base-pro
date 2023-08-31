package com.sloth.functions.api;

public class HttpError extends RuntimeException {

    public static final int CODE_NET_TIMEOUT = 408;
    public static final String MSG_NET_TIMEOUT = "网络超时";

    public static final int CODE_NET_DISCONNECT = 409;
    public static final String MSG_NET_DISCONNECT = "网络异常";

    private int errorCode;

    public HttpError(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
