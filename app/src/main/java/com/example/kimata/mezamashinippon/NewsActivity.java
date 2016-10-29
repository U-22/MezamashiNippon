package com.example.kimata.mezamashinippon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
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
    private int siteIndex = 0;
    private int newsLimitNumber;
    private GridView gridView;
    private TextView newsTitle;
    private TextToSpeech announcer;
    private HashMap<String, String> myHash;
    private Bundle myBundle;
    private MNAsyncHtmlLoaderClass m_asyncHtmlLoaderClass;
    private MNAsyncImageLoaderClass m_asyncImageLoaderClass;
    private MediaPlayer m_player;
    private AudioManager m_manager;
    private RelativeLayout m_alarmStopLayout;
    private Button m_stopButton;

    //スレッドID
    public static final int HTML_LOADER = 0;
    public static final int IMAGE_LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //各種設定
        //フラグの初期化
        endFlag = false;
        //SPからsiteリストの復元
        int siteNumber = loadMNSiteList();
        if(siteNumber == 0)
        {
            Intent intent = new Intent();
            intent.setClassName("com.example.kimata.mezamashinippon","com.example.kimata.mezamashinippon.MainActivity");
            intent.putExtra("ERROR_STATE", MNStringResources.NO_SITE);
            startActivity(intent);
            return;
        }
        //設定データの取得
        loadSettingData();
        //ttsに渡すhashmapの初期化
        myHash = new HashMap<String, String>();
        myHash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "announcer");
        //ttsに渡すbundleの初期化(APILV 22)
        myBundle = new Bundle();
        myBundle.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //起動方法の取得
        Intent current = getIntent();
        int state = current.getIntExtra("STATE", 0);
        if(state == MNStringResources.ACTIVITY_START_BY_ALARM)
        {
            //アラームから起動
            setupAlarmStopLayout();
            audioSetup();
        }else if(state == MNStringResources.ACTIVITY_START_BY_BUTTON)
        {
            //ボタンから起動
            setContentView(R.layout.activity_news);
            //gridviewの生成
            gridView = (GridView)findViewById(R.id.articleImage);
            //タイトルの初期化
            newsTitle = (TextView)findViewById(R.id.newsTitle);
            setAsyncHtmloLoaderClass();
        }
        //setupAlarmStopLayout();
        //setContentView(R.layout.activity_news);
    }

    @Override
    public void onPause()
    {
        releaseTts();
        audioStop();
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
            intent.putExtra("ERROR_STATE", MNStringResources.NO_HTML);
            startActivity(intent);
            return;
        }
        //announcerの初期化
        if(announcer == null) {
            announcer = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    if (i != TextToSpeech.ERROR) {
                        int result = announcer.setLanguage(Locale.JAPAN);
                        //発話終了イベントリスナーを登録
                        result = announcer.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
                            @Override
                            public void onUtteranceCompleted(String utteranceId) {
                                //次の記事を取得
                                boolean allArtcileFlag = isReadAllArticles();
                                boolean allSiteFlag = isReadAllSites();
                                if(allSiteFlag)
                                {
                                    return;
                                }
                                if(allArtcileFlag)
                                {
                                    setAsyncHtmloLoaderClass();
                                }else{
                                    setAsyncImageLoaderClass();
                                }
                            }
                        });
                        /*result = announcer.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                            @Override
                            public void onStart(String s) {

                            }

                            @Override
                            public void onDone(String s) {
                                //次の記事を取得
                                Log.d("読み上げ", "onDone: 読み上げ終了");
                                setAsyncImageLoaderClass();
                            }

                            @Override
                            public void onError(String s) {

                            }
                        });*/
                        setAsyncImageLoaderClass();
                    }
                }
            });
        }else{
            setAsyncImageLoaderClass();
        }
        Log.d("load", "htmlLoaderFinished: ");
    }

    @Override
    public  void MNAsyncImageLoaderCallbacks(Boolean result)
    {
        if(result != true)
        {
            Toast.makeText(NewsActivity.this, "画像を取得できませんでした", Toast.LENGTH_SHORT).show();
        }
        readArticle();
        /*if(isReadAllArticles() && !isReadAllSites())
        {
            setAsyncHtmloLoaderClass();
        }*/
        Log.d("load", "imageLoaderFinished: ");
    }

    private int loadMNSiteList(){
        SharedPreferences sp = getSharedPreferences(MNStringResources.SETTING_FILE_NAME, MODE_PRIVATE);
        String siteList = sp.getString("MNSiteList", "");
        Gson gson = new Gson();
        MNSite[] siteArray = gson.fromJson(siteList, MNSite[].class);
        if(siteArray != null && siteArray.length != 0)
        {
            m_siteList = new ArrayList<MNSite>(Arrays.asList(siteArray));
        }
        return m_siteList.size();
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

        newsTitle.setText(m_htmlList.get(articleIndex).getMainTitle());
        //読み上げる文章の生成
        String text;
        if(articleIndex == 0)
        {
            text = m_siteList.get(siteIndex).getUrl() + "の最初のニューすです\n" + newsTitle.getText() + "\n"; //+ m_htmlList.get(articleIndex).getMainContents();
        }else {
            text = m_siteList.get(siteIndex).getUrl() + "の次のニューすです\n" + newsTitle.getText() + "\n";// + m_htmlList.get(articleIndex).getMainContents();
        }
        announcer.speak(text, TextToSpeech.QUEUE_FLUSH, myHash);
        articleIndex++;
    }

    private void setAsyncImageLoaderClass()
    {
        if(m_asyncImageLoaderClass == null)
        {
            m_asyncImageLoaderClass = new MNAsyncImageLoaderClass(this, getApplicationContext(), m_htmlList.get(articleIndex));
            getSupportLoaderManager().initLoader(IMAGE_LOADER, null, m_asyncImageLoaderClass);
        }else{
            m_asyncImageLoaderClass = new MNAsyncImageLoaderClass(this, getApplicationContext(), m_htmlList.get(articleIndex));
            getSupportLoaderManager().restartLoader(IMAGE_LOADER, null, m_asyncImageLoaderClass);
        }
    }

    private void setAsyncHtmloLoaderClass()
    {
        Log.d("news", "setAsyncHtmloLoaderClass: " + siteIndex);
        if(m_asyncHtmlLoaderClass == null)
        {
            m_asyncHtmlLoaderClass = new MNAsyncHtmlLoaderClass(this, getApplicationContext(), m_siteList.get(siteIndex));
            getSupportLoaderManager().initLoader(HTML_LOADER, null, m_asyncHtmlLoaderClass);
        }else{
            m_asyncHtmlLoaderClass = new MNAsyncHtmlLoaderClass(this, getApplicationContext(), m_siteList.get(siteIndex));
            getSupportLoaderManager().restartLoader(HTML_LOADER, null, m_asyncHtmlLoaderClass);
        }
    }

    private boolean isReadAllArticles()
    {
        if(articleIndex >= articleCount)
        {
            articleIndex = 0;
            siteIndex++;
            return true;
        }
        return false;
    }

    private boolean isReadAllSites()
    {
        if(siteIndex >= m_siteList.size())
        {
            siteIndex = 0;
            return true;
        }
        return false;
    }

    //アラームなので，音量を最大にして再生
    private void audioSetup()
    {
        m_player = MediaPlayer.create(this, R.raw.alarm);
        m_player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        m_manager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int maxVol = m_manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        m_manager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVol, 0);
        m_player.setLooping(true);
        m_player.start();
    }

    private void audioStop()
    {
        if(m_player != null) {
            m_player.stop();
            m_player.release();
            m_player = null;
        }
    }

    private void changeLyaout()
    {
        setContentView(R.layout.activity_news);
        //gridviewの生成
        gridView = (GridView)findViewById(R.id.articleImage);
        //タイトルの初期化
        newsTitle = (TextView)findViewById(R.id.newsTitle);
    }

    private void setupAlarmStopLayout()
    {
        //リレイティブレイアウトの設定
        m_alarmStopLayout = new RelativeLayout(this);
        m_alarmStopLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        ));
        setContentView(m_alarmStopLayout);
        m_stopButton = new Button(this);
        m_stopButton.setText("STOP!");
        m_stopButton.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        ));
        m_stopButton.setBackgroundColor(Color.parseColor("#FF009688"));
        //TODO: 画面の大きさに合わせる
        m_stopButton.setTextSize(50);
        m_stopButton.setTextColor(Color.parseColor("#ffffff"));
        m_stopButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    audioStop();
                    changeLyaout();
                    setAsyncHtmloLoaderClass();
                }
            }
        );
        m_alarmStopLayout.addView(m_stopButton);
    }



}
