package com.example.kimata.mezamashinippon;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by umino on 16/09/21.
 */
public class MNAsyncHtmlLoader extends android.support.v4.content.AsyncTaskLoader<ArrayList<MNHtml>>{

    private ArrayList<MNSite> m_siteList = new ArrayList<MNSite>();
    private ArrayList<MNHtml> m_htmlList = new ArrayList<MNHtml>();

    public MNAsyncHtmlLoader(Context context, ArrayList<MNSite> siteList)
    {
        super(context);
        m_siteList = siteList;
    }


    @Override
    //TODO:ユーザーの設定を参照するようにする
    public ArrayList<MNHtml> loadInBackground(){
        for(MNSite site : m_siteList)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.set(2016, 9, 11);
            site.addNewArticle(calendar.getTime());
            site.generateHtml();
            m_htmlList.addAll(site.getHtmlList());
        }
        for(MNHtml html : m_htmlList)
        {
            html.getPicture();
            html.getMainTitle();
        }
        return m_htmlList;
    }

    @Override
    protected void onStartLoading(){
        forceLoad();
    }
}
