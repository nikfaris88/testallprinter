package com.example.printerlibs;

public class Response implements IResponse{

    private String response = "";

    @Override
    public void onSuccess(String result) {
        response = result;
    }

    @Override
    public void onFailed(String result) {
        response = result;
    }

    @Override
    public void onError(Throwable throwable) {
        response = throwable.getLocalizedMessage();
    }

    public String getResponse() {
        return response;
    }
}
