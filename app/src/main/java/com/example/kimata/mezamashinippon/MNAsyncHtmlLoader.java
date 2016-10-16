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

    private MNSite m_site;
    private ArrayList<MNHtml> m_htmlList = new ArrayList<MNHtml>();

    public MNAsyncHtmlLoader(Context context, MNSite site)
    {
        super(context);
        m_site = site;
    }


    @Override
    //TODO:ユーザーの設定を参照するようにする
    public ArrayList<MNHtml> loadInBackground(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 9, 13);
        m_site.addNewArticle(calendar.getTime());
        m_site.generateHtml();
        m_htmlList.addAll(m_site.getHtmlList());
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
