package com.example.kimata.mezamashinippon;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
            if(tag.equals("pubDate"))
            {
                return pubDateFormat.parse(text);
            }else if(tag.equals("dc|date"))
            {
                return dc_DateFormat.parse(text);
            }

        }catch (ParseException e){
            e.printStackTrace();
            return null;
        }
        throw new AssertionError(tag + "is not supported to use for Date");
    }
    //ファイルからバイナリを読み込み文字列に変換
    public static String readFiletoString(FileInputStream is)
    {
        ArrayList<Byte> ByteList = new ArrayList<Byte>();
        byte[] byteArray = new byte[10];
        int i = 0;
        while (i != -1)
        {
            try{
                i = is.read(byteArray, 0, 10);
                for(int j = 0; j < i; j++)
                {
                    Byte tempByte = new Byte(byteArray[j]);
                    ByteList.add(tempByte);

                }
            }catch (NullPointerException e) {
                e.printStackTrace();
            }catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        Byte[] ByteArray = new Byte[ByteList.size()];
        ByteArray = ByteList.toArray(ByteArray);
        byte[] bytes = Byte2byte(ByteArray);
        String returnString = new String(bytes);
        return returnString;
    }

    public static byte[] Byte2byte(Byte[] ByteArray)
    {
        int size = ByteArray.length;
        byte[] byteArray = new byte[size];
        for(int i = 0; i < size; i++)
        {
            byteArray[i] = ByteArray[i];
        }
        return byteArray;
    }
}
