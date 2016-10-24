package com.example.kimata.mezamashinippon;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by umino on 16/09/21.
 */
public class MNAsyncHtmlLoader extends android.support.v4.content.AsyncTaskLoader<ArrayList<MNHtml>>{

    private MNSite m_site;
    private int m_newsLimitNumber;
    private ArrayList<MNHtml> m_htmlList = new ArrayList<MNHtml>();

    public MNAsyncHtmlLoader(Context context, MNSite site)
    {
        super(context);
        m_site = site;
        loadSettingData();
    }


    @Override
    //TODO:ユーザーの設定を参照するようにする
    public ArrayList<MNHtml> loadInBackground(){
        m_site.addNewArticle(m_newsLimitNumber);
        m_site.generateHtml();
        m_htmlList.addAll(m_site.getHtmlList());
        for(MNHtml html : m_htmlList)
        {
            //html.getPicture();
            html.getMainTitle();
        }
        return m_htmlList;
    }

    @Override
    protected void onStartLoading(){
        forceLoad();
    }

    private void loadSettingData()
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        m_newsLimitNumber = sp.getInt("NewsNumber_key", 0);
    }
}
