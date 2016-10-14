package com.example.kimata.mezamashinippon;

import android.util.Log;
import android.util.StringBuilderPrinter;
import android.util.Xml;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by umino on 16/08/19.
 */
public class MNSite {
    //m_urlはニュースサイトのトップページのURL
    //サイトの登録は、トップページのURL入力→パス生成用に任意の記事を表示して、始点と終点を選択の手順で行う
    private String m_url;
    private String m_rssUr;
    private String m_startIdentifier;
    private String m_startClassName;
    private String m_startClassNameParent;
    private ArrayList<String> m_newArticleList;
    private ArrayList<MNHtml> m_HtmlList;

    MNSite(String url)
    {
        m_url = url;
        m_rssUr = new String();
        m_startIdentifier = new String();
        m_newArticleList = new ArrayList<String>();
        m_HtmlList = new ArrayList<MNHtml>();
        m_startClassName = new String();
        m_startClassNameParent = new String();
    }

    //setter
    void setStartIdentifier(String start)
    {
        if(!start.isEmpty())
        {
            m_startIdentifier = start;
        }
    }

    //getter
    String getRssUrl()
    {
        return m_rssUr;
    }
    ArrayList<MNHtml> getHtmlList(){return m_HtmlList;}
    String getStartClassName() {return m_startClassName;}

    //スタート指定子が含まれるhtml要素のクラス名を取得
    void findStartClassName(final String sampleUrl)
    {
        if(!m_startIdentifier.isEmpty())
        {
            try{
                Document targetDoc = Jsoup.connect(sampleUrl).get();
                Elements candinateElements = targetDoc.getElementsContainingOwnText(m_startIdentifier);
                Element startElement = candinateElements.first();
                m_startClassName = startElement.className();
                if(m_startClassName.isEmpty())
                {
                    //スタート指定子が含まれるhtml要素そのクラス名が無かった場合、親のクラス名を取得する
                    Element parent = startElement.parent();
                    m_startClassNameParent = parent.className();
                }
            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    String getHtml(final String sampleUrl)
    {
        String html = new String();
        try
        {
            Document targetDoc = Jsoup.connect(sampleUrl).get();
            html = targetDoc.html();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return html;
    }

    //RSSフィードのURLを取得
    boolean findRssUrl()
    {
        try{
            Document targetDoc = Jsoup.connect(m_url).get();
            Elements candinateElements = targetDoc.getElementsByTag("link");
            for(Element element : candinateElements)
            {
                String type = element.attr("type");
                if(type.equals("application/rss+xml"))
                {
                    m_rssUr = new String(element.attr("href"));
                    return true;
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    //新しく追加された記事を追加
    void addNewArticle(Date baseDate)
    {
        try {
            URL url = new URL(m_rssUr);
            URLConnection connection = url.openConnection();

            BufferedReader inputStream = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();

            //xmlパーサーの準備
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream);

            int eventType = parser.getEventType();
            boolean isPubDate = false;
            while (eventType != XmlPullParser.END_DOCUMENT){
                if(eventType == XmlPullParser.START_TAG)
                {
                    String tagName = parser.getName();
                    if(tagName.equals("dc:date"))
                    {
                        isPubDate = false;
                    }else if(tagName.equals("pubDate"))
                    {
                        isPubDate = true;
                    }
                }
                eventType = parser.next();
            }
            //jsoupを用いてパース
            Document targetDoc = Jsoup.connect(m_rssUr).get();
            Elements items = targetDoc.select("item");
            for (Element element : items){
                Date date = new Date();
                if(isPubDate)
                {
                    date = MNUtil.convertRssDate("pubDate",element.select("pubDate").text());
                }else{
                    date = MNUtil.convertRssDate("dc|date",element.select("dc|date").text());
                }
                Log.d("date", date.toString());
                if(date.after(baseDate))
                {
                    m_newArticleList.add(element.select("link").text());
                }
            }
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch (XmlPullParserException e){
            e.printStackTrace();
        }
        /*try{
            Document targetDoc = Jsoup.connect(m_rssUr).get();
            Elements items = targetDoc.select("item");
            for (Element element : items){
                Date date = MNUtil.convertRssDate("dc|date",element.select("dc|date").text());
                Log.d("date", date.toString());
                if(date.after(baseDate))
                {
                    m_newArticleList.add(element.select("link").text());
                }
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }*/
    }


    //MNHtmlの生成
    //最初にm_HtmlListを初期化
    boolean generateHtml()
    {
        m_HtmlList.clear();
        if(m_newArticleList.isEmpty())
        {
            return false;
        }
        for(String url : m_newArticleList)
        {
            MNHtml html = new MNHtml(url, m_startClassName, m_startClassNameParent);
            html.generateMainContent();
            m_HtmlList.add(html);
        }
        return true;
    }

}
