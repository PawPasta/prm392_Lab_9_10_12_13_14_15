package com.prm392_sp26.se182138_lab12;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Integer> counter = new MutableLiveData<>(0);

    public MutableLiveData<Integer> getCounter() {
        return counter;
    }

    public void increment() {
        Integer value = counter.getValue();
        if (value == null) {
            value = 0;
        }
        counter.setValue(value + 1);
    }
}
