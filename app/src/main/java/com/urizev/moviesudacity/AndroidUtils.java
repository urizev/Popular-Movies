package com.urizev.moviesudacity;

import android.os.Looper;

public class AndroidUtils {
    public static void assertMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new RuntimeException("This code should be running on main thread");
        }
    }
}
