package com.example.kimata.mezamashinippon;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by umino on 16/08/21.
 */
public class MNUtil {
    //日本時間に変更
    public static Date convertRssDate(String tag, String text)
    {
        SimpleDateFormat pubDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
        SimpleDateFormat dc_DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz", Locale.ENGLISH);
        try{
            switch (tag){
                case "pubDate":
                    return pubDateFormat.parse(text);
                case "dc|date":
                    return dc_DateFormat.parse(text);
            }
        }catch (ParseException e){
            e.printStackTrace();
            return null;
        }
        throw new AssertionError(tag + "is not supported to use for Date");
    }
}
