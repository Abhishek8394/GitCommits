package com.apbytes.gitcommits;

import android.util.Log;

/**
 * Created by Abhishek on 6/8/2018.
 */

public class Utility {
    private static boolean DEBUG_MODE = true;
    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm'Z'";

    /**
     * For logging debug only mode statements.
     * @param tag String
     * @param message String
     */
    public static void log(String tag, String message){
        if(DEBUG_MODE){
            Log.d(tag,message);
        }
    }
}
