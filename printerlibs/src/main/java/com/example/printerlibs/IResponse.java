package com.example.printerlibs;

public interface IResponse {

    void onSuccess(String result);
    void onFailed(String result);

    void onError(Throwable throwable);
}
