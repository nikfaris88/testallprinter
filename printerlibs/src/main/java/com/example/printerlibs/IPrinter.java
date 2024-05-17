package com.example.printerlibs;

import com.wisepos.smartpos.WisePosException;

public interface IPrinter {
    String getModel();
    void initialise(PrinterManager.PrinterCallback callback) throws WisePosException;
    void print(Object args, PrinterManager.PrinterCallback callback);
}

