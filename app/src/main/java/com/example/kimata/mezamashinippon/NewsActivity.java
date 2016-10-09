package com.example.kimata.mezamashinippon;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class NewsActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<ArrayList<MNHtml>>{

    private ArrayList<MNSite> m_siteList = new ArrayList<MNSite>();
    private ArrayList<MNHtml> m_htmlList = new ArrayList<MNHtml>();
    private boolean endFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        //フラグの初期化
        endFlag = false;
        //SPからsiteリストの復元
        loadMNSiteList();
        //LoaderManagerの初期化
        getSupportLoaderManager().initLoader(0,null,this);
        //LoderManagerの実行

    }

    @Override
    public Loader<ArrayList<MNHtml>> onCreateLoader(int id, Bundle args)
    {
        return new MNAsyncHtmlLoader(this, m_siteList);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MNHtml>> loader, ArrayList<MNHtml> htmlList)
    {
        m_htmlList = htmlList;
        Log.d("load", "onLoadFinished: ");
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MNHtml>> loader)
    {

    }


    private void loadMNSiteList(){
        SharedPreferences sp = getSharedPreferences(MNStringResources.SETTING_FILE_NAME, MODE_PRIVATE);
        String siteList = sp.getString("MNSiteList", "");
        Gson gson = new Gson();
        MNSite[] siteArray = gson.fromJson(siteList, MNSite[].class);
        if(siteArray != null && siteArray.length != 0)
        {
            m_siteList = new ArrayList<>(Arrays.asList(siteArray));
        }
    }

}
