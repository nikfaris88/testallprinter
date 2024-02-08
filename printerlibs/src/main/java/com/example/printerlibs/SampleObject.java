package com.example.printerlibs;

import org.json.JSONException;
import org.json.JSONObject;

public class SampleObject {

    private String name;
    private int age;

    public SampleObject(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public JSONObject printInfo() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("Name", name);
        obj.put("Age", age);

        return obj;
    }
}
