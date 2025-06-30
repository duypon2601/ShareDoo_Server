package com.server.ShareDoo.dto.response;

public class RestResponse<T> {
    private int statusCode;
    private String error;
    private Object message;
    private T data;

    public RestResponse() {
    }

    public RestResponse(int statusCode, String error, Object message, T data) {
        this.statusCode = statusCode;
        this.error = error;
        this.message = message;
        this.data = data;
    }

    public static <T> RestResponse<T> success(T data, String message) {
        return new RestResponse<>(200, null, message, data);
    }

    public static <T> RestResponse<T> error(String error, String message) {
        return new RestResponse<>(400, error, message, null);
    }

    public static <T> RestResponse<T> error(int statusCode, String error, String message) {
        return new RestResponse<>(statusCode, error, message, null);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}