package com.apbytes.gitcommits.Utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Abhishek on 6/8/2018.
 */

public class Utility {
    private static boolean DEBUG_MODE = true;
    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String LOCAL_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm";

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

    public static Date stringToISODate(String isoDateString) throws ParseException {
        SimpleDateFormat isoSdf = new SimpleDateFormat(ISO_DATE_FORMAT);
        return isoSdf.parse(isoDateString);
    }

    public static String ISODatetoString(Date date){
        SimpleDateFormat isoSdf = new SimpleDateFormat(ISO_DATE_FORMAT);
        isoSdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return isoSdf.format(date);
    }

    public static Date convertISOtoLocale(String isoDateString) throws ParseException {
        SimpleDateFormat isoSdf = new SimpleDateFormat(ISO_DATE_FORMAT);
        isoSdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat localSdf = new SimpleDateFormat(LOCAL_DATE_FORMAT);
        localSdf.setTimeZone(TimeZone.getDefault());
        // parse string as UTC. Output converted string. Parse back into date object.
        Date d = localSdf.parse(localSdf.format(isoSdf.parse(isoDateString)));
        return d;
    }
}
