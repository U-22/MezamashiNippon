package com.example.kimata.mezamashinippon;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class NewsActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<ArrayList<MNHtml>>{

    private ArrayList<MNSite> m_siteList = new ArrayList<MNSite>();
    private ArrayList<MNHtml> m_htmlList = new ArrayList<MNHtml>();
    private boolean endFlag;
    private int articleCount = 0;
    private int articleIndex = 0;
    private GridView gridView;
    private TextToSpeech announcer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        //フラグの初期化
        endFlag = false;
        //SPからsiteリストの復元
        loadMNSiteList();
        //gridviewの生成
        gridView = (GridView)findViewById(R.id.articleImage);
        //announcerの初期化
        announcer = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR)
                {
                    announcer.setLanguage(Locale.JAPAN);
                }
            }
        });
        //LoaderManagerの初期化
        getSupportLoaderManager().initLoader(0,null,this);
        //LoderManagerの実行

    }

    @Override
    public void onPause()
    {
        if(announcer != null)
        {
            announcer.stop();
            announcer.shutdown();
        }
        super.onPause();
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
        //記事数の取得
        articleCount = m_htmlList.size();
        readArticle(articleIndex);
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

    private void readArticle(int nextIndex)
    {
        gridView.setAdapter(new MNArticleImageAdapter(this, m_htmlList.get(nextIndex).getImageList()));
        announcer.speak(m_htmlList.get(nextIndex).getMainContents(), TextToSpeech.QUEUE_FLUSH, null);
    }


}
