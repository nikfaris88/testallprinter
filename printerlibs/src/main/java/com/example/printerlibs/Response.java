package com.example.printerlibs;

public class Response implements IResponse{

    String result = "";
    @Override
    public void code(int code) {

    }

    @Override
    public void message(String result) {
        this.result = result;
    }

    public String getResult(){
        return result;
    }
}
