package com.example.printerlibs;

public interface IPrinter {
    String getModel();
    void initialise(PrinterManager.PrinterCallback callback);
    void print(Object args, PrinterManager.PrinterCallback callback);
}

