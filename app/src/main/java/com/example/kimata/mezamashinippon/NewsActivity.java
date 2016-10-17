package com.example.kimata.mezamashinippon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
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
import java.util.HashMap;
import java.util.Locale;

public class NewsActivity extends FragmentActivity implements MNLoaderCallbacks{

    private ArrayList<MNSite> m_siteList = new ArrayList<MNSite>();
    private ArrayList<MNHtml> m_htmlList = new ArrayList<MNHtml>();
    private boolean endFlag;
    private int articleCount = 0;
    private int articleIndex = 0;
    private int newsLimitNumber;
    private GridView gridView;
    private TextView newsTitle;
    private TextToSpeech announcer;
    private HashMap<String, String> myHash;
    private Bundle myBundle;
    private MNAsyncHtmlLoaderClass m_asyncHtmlLoaderClass;

    //スレッドID
    public static final int HTML_LOADER = 0;
    public static final int IMAGE_LOADER = 1;
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
        //ttsに渡すhashmapの初期化
        myHash = new HashMap<>();
        myHash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "announcer");
        //ttsに渡すbundleの初期化(APILV 22)
        myBundle = new Bundle();
        myBundle.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");

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
        //getSupportLoaderManager().initLoader(0,null,this);
        //LoderManagerの実行

        m_asyncHtmlLoaderClass = new MNAsyncHtmlLoaderClass(this, getApplicationContext(), m_siteList.get(0));
        getSupportLoaderManager().initLoader(HTML_LOADER, null, m_asyncHtmlLoaderClass);

    }

    @Override
    public void onPause()
    {
        releaseTts();
        super.onPause();
    }

    @Override
    public void MNAsyncHtmlLoaderCallbacks(ArrayList<MNHtml> htmlList)
    {
        m_htmlList = htmlList;
        articleCount = m_htmlList.size();
        if(articleCount == 0)
        {
            Intent intent = new Intent();
            intent.setClassName("com.example.kimata.mezamashinippon","com.example.kimata.mezamashinippon.MainActivity");
            startActivity(intent);
            //TODO 何かメッセージをだす
            return;
        }
        //announcerの初期化
        announcer = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR)
                {
                    int result = announcer.setLanguage(Locale.JAPAN);
                    //発話終了イベントリスナーを登録
                    result = announcer.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String s) {

                        }

                        @Override
                        public void onDone(String s) {
                            //次の記事を取得
                            Log.d("読み上げ", "onDone: 読み上げ終了");
                        }

                        @Override
                        public void onError(String s) {

                        }
                    });
                    readArticle();
                }
            }
        });
        Log.d("load", "onLoadFinished: ");
    }

    @Override
    public  void MNAsyncImageLoaderCallbacks()
    {

    }



    /*@Override
    public Loader<ArrayList<MNHtml>> onCreateLoader(int id, Bundle args)
    {
        return new MNAsyncHtmlLoader(this, m_siteList.get(0));
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MNHtml>> loader, ArrayList<MNHtml> htmlList)
    {
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
        //announcerの初期化
        announcer = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR)
                {
                    int result = announcer.setLanguage(Locale.JAPAN);
                    //発話終了イベントリスナーを登録
                    result = announcer.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String s) {

                        }

                        @Override
                        public void onDone(String s) {
                            //次の記事を取得
                            Log.d("読み上げ", "onDone: 読み上げ終了");
                        }

                        @Override
                        public void onError(String s) {

                        }
                    });
                    readArticle();
                }
            }
        });
        Log.d("load", "onLoadFinished: ");
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MNHtml>> loader)
    {

    }*/


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
        gridView.setAdapter(new MNArticleImageAdapter(this, m_htmlList.get(articleIndex).getImageList()));

        //TODO：APIレベルに応じて処理を分ける

        newsTitle.setText(m_htmlList.get(articleIndex).getMainTitle());
        if(Build.VERSION.RELEASE.startsWith("5"))
        {
            announcer.speak(m_htmlList.get(articleIndex).getMainContents(), TextToSpeech.QUEUE_FLUSH, null, "announcer");
        }else{
            announcer.speak(m_htmlList.get(articleIndex).getMainContents(), TextToSpeech.QUEUE_FLUSH, myHash);
        }
    }


}
