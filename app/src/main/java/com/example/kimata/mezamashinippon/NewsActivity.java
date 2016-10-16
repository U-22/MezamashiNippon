package com.example.kimata.mezamashinippon;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

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
    private int newsLimitNumber;
    private GridView gridView;
    private TextView newsTitle;
    private TextToSpeech announcer;
    //debug用
    private Button buttonDebug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        //フラグの初期化
        endFlag = false;
        //SPからsiteリストの復元
        loadMNSiteList();
        //設定データの取得
        loadSettingData();
        //gridviewの生成
        gridView = (GridView)findViewById(R.id.articleImage);
        //タイトルの初期化
        newsTitle = (TextView)findViewById(R.id.newsTitle);

        buttonDebug = (Button)findViewById(R.id.button_debug);
        buttonDebug.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                articleIndex++;
                readArticle();
            }
        });

        //announcerの初期化
        /*announcer = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR)
                {
                    announcer.setLanguage(Locale.JAPAN);
                }
            }
        });*/
        //LoaderManagerの初期化
        getSupportLoaderManager().initLoader(0,null,this);
        //LoderManagerの実行

    }

    @Override
    public void onPause()
    {
        releaseTts();
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
        //announcerの初期化
        announcer = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR)
                {
                    announcer.setLanguage(Locale.JAPAN);
                    readArticle();
                }
            }
        });
        m_htmlList = htmlList;
        //記事数の取得
        articleCount = m_htmlList.size();
        if(articleCount == 0)
        {
            Intent intent = new Intent();
            intent.setClassName("com.example.kimata.mezamashinippon","com.example.kimata.mezamashinippon.MainActivity");
            startActivity(intent);
            //TODO 何かメッセージをだす
            return;
        }
        //readArticle();
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

    private void loadSettingData()
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        newsLimitNumber = sp.getInt("NewsNumber_key", 0);
    }

    private void releaseTts()
    {
        if(announcer != null)
        {
            announcer.stop();
            announcer.shutdown();
        }
    }


    private void readArticle()
    {
        //設定記事数が現在の記事数よりも大きい場合上限値を変更する
        if(newsLimitNumber > articleCount)
        {
            newsLimitNumber = articleCount;
        }
        //ループでニュースを読み上げる
        //for(int index = 0; index < newsLimitNumber; index++)
        //{
        int index = 0;
            gridView.setAdapter(new MNArticleImageAdapter(this, m_htmlList.get(index).getImageList()));

            //APIレベルに応じて処理を分ける

            newsTitle.setText(m_htmlList.get(index).getMainTitle());
            if(Build.VERSION.RELEASE.startsWith("5"))
            {
                announcer.speak(m_htmlList.get(index).getMainContents(), TextToSpeech.QUEUE_FLUSH, null, null);
            }else{
                announcer.speak(m_htmlList.get(index).getMainContents(), TextToSpeech.QUEUE_FLUSH, null);
            }
       // }
        /*if((nextIndex >= newsLimitNumber) || (nextIndex > articleCount))
        {
            releaseTts();
        }else {
            gridView.setAdapter(new MNArticleImageAdapter(this, m_htmlList.get(nextIndex).getImageList()));

            //APIレベルに応じて処理を分ける

            newsTitle.setText(m_htmlList.get(nextIndex).getMainTitle());
            if(Build.VERSION.RELEASE.startsWith("5"))
            {
                announcer.speak(m_htmlList.get(nextIndex).getMainContents(), TextToSpeech.QUEUE_FLUSH, null, null);
            }else{
                announcer.speak(m_htmlList.get(nextIndex).getMainContents(), TextToSpeech.QUEUE_FLUSH, null);
            }
        }
        */
    }


}
