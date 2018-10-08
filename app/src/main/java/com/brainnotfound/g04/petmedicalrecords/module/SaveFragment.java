package com.brainnotfound.g04.petmedicalrecords.module;

public class SaveFragment {

    private static SaveFragment saveFragmentInstance;
    private static String name;

    private SaveFragment() {}

    public static SaveFragment getSaveFragmentInstance() {
        if(saveFragmentInstance == null) {
            saveFragmentInstance = new SaveFragment();
        }
        return saveFragmentInstance;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        SaveFragment.name = name;
    }
}
