package com.fox.utils;

import java.sql.Timestamp;

/**
 * Created by shuangf on 1/16/18.
 */
public class TimeHelper {

    static Timestamp timeStamp;

    public static String getCurrentMiliTime() {
        timeStamp = new Timestamp(System.currentTimeMillis());
        return String.valueOf(timeStamp.getTime());
    }
}
