package com.example.printerlibssampleapp;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class PrinterViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(ActivityViewModel.class)) {
            try {
                return modelClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace(); // Log the error for debugging
                throw new IllegalArgumentException("Cannot create an instance of " + modelClass, e);
            }
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
