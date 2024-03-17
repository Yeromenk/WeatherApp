package com.example.weatherapp;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;

import java.util.Locale;

public class FragmentFactory {

    public static <T extends Fragment> T newInstance(Class<T> tClass, String... args) {
        Bundle bundle = new Bundle();

        for (int i = 0; i < args.length; i++) {
            String key = String.format(Locale.getDefault(), "arg%d", i);
            bundle.putString(key, args[i]);
        }

        return newInstance(tClass, bundle);
    }


    public static <T extends Fragment> T newInstance(Class<T> tClass) {
        return newInstance(tClass, new Bundle());
    }

    private static <T extends Fragment> T newInstance(Class<T> tClass, Bundle bundle) {
        T fragment = null;
        try {
            fragment = tClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            Log.e("fragment", "Unable to instantiate " + e.getMessage());
        }

        assert fragment != null;
        fragment.setArguments(bundle);

        return fragment;
    }
}
