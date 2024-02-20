package com.example.printerlibs;

import android.content.Context;

public interface Printer {
    String getModel();
    void initialise();
    void print(Object args);
}

