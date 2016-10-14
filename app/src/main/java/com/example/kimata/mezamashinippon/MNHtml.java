package com.example.kimata.mezamashinippon;

/**
 * Created by umino on 16/08/11.
 */
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MNHtml {
    private Document m_targetDoc;
    private String m_targetUrl;
    private String m_mainContents;
    private String m_mainTitle;
    private String m_startClassName;
    private String m_startClassNameParent;
    private ArrayList<Bitmap> m_imageList;

    //コンストラクタ
    MNHtml(String url, String startClassName, String startClassNameParent)
    {
        m_targetUrl = url;

        try {
            m_targetDoc = Jsoup.connect(m_targetUrl).get();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        m_imageList = new ArrayList<Bitmap>();
        m_startClassName = startClassName;
        m_startClassNameParent = startClassNameParent;
        m_mainContents = new String();
        //m_mainTitle = m_targetDoc.title();
    }

    //setter

    //getter
    String getMainContents() {return m_mainContents;}
    ArrayList<Bitmap> getImageList() {return m_imageList;}
    String getMainTitle() {return m_mainTitle;}



    //本文取得関数群
    //パスを使う新バージョン
    /*void findStartElement()
    {
        Elements candinates = m_targetDoc.getAllElements();
        for (String tagName : m_Path)
        {
            if(tagName.equals("#root"))
            {
                continue;
            }
            if(candinates.select(tagName).isEmpty())
            {
                break;
            }
            candinates = candinates.select(tagName);
        }
        if(!candinates.isEmpty())
        {
            m_startElement = candinates.first();
        }
    }*/
    /*void findEndElement()
    {
        Elements candinates = m_targetDoc.getAllElements();
        for (String tagName : m_Path)
        {
            if(tagName.equals("#root"))
            {
                continue;
            }
            if(candinates.select(tagName).isEmpty())
            {
                break;
            }
            candinates = candinates.select(tagName);
        }
        if(!candinates.isEmpty())
        {
            m_endElement = candinates.last();
        }
    }*/
    //箇条書きなど、htmlのスタイルによっては順番が前後することあり
    //要改良
    /*boolean generateMainContents()
    {
        //リンクは無視する
        if((m_startElement != null) && (m_endElement != null))
        {
            //スタートノードからエンドノードまでのテキストを連結
            boolean startFlag = false;
            boolean endFlag = false;
            String mainContents = "";
            for(Element element : m_targetDoc.getAllElements())
            {
                if((element.tagName().equals("a") || (element.tagName().equals("A"))))
                {
                    continue;
                }
                if(element.equals(m_startElement))
                {
                    startFlag = true;
                }
                if(element.equals(m_endElement))
                {
                    endFlag = true;
                    startFlag = false;
                }
                if(startFlag)
                {
                    mainContents = mainContents + element.ownText();
                    mainContents = mainContents + "\n";
                }
                if(endFlag)
                {
                    mainContents = mainContents + element.ownText();
                    mainContents = mainContents + "\n";
                    break;
                }
            }
            m_mainContents = mainContents;
            return true;
        }else{
            return false;
        }
    }*/

    //本文取得関数
    void generateMainContent()
    {
        //<strong("[^"]*"|'[^']*'|[^'">])*>|</strong>
        //正規表現を用いて、邪魔なタグを取り除く
        String html = m_targetDoc.html();
        String regex = "<strong(\"[^\"]*\"|'[^']*'|[^'\">])*>|</strong>|<a(\"[^\"]*\"|'[^']*'|[^'\">])*>|</a>";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        html = matcher.replaceAll("");
        //html文字列からdocumentを生成
        Document replacedDoc = Jsoup.parse(html);
        String content = new String();
        Elements candinates;
        if(m_startClassName != null && !m_startClassName.isEmpty())
        {
            candinates = replacedDoc.getElementsByClass(m_startClassName);
        }else if(m_startClassNameParent != null && !m_startClassNameParent.isEmpty())
        {
            candinates = replacedDoc.getElementsByClass(m_startClassNameParent).first().children();
        }else{
            m_mainContents = "本文を取得できませんでした";
            return;
        }
        for(Element element : candinates)
        {
            if(element.ownText() != null && !element.ownText().isEmpty()) {
                content += element.ownText();
                content += "\n";
            }
        }
        m_mainContents = content;
    }

    //記事内の画像を取得する関数
    //記事に関係の内画像までとってきてしまうため要改良
    void getPicture()
    {
        Elements imageElement = m_targetDoc.getElementsByTag("img");
        Elements linkElement = m_targetDoc.getElementsByTag("a");
        //全てのリンクの取得
        ArrayList<String> linkList = new ArrayList<String>();
        for (final Element element : linkElement)
        {
            linkList.add(element.attr("href"));
        }
        for(final Element element : imageElement)
        {
            try {
                URL url = new URL(element.attr("src"));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                Log.d("url", "getPicture: " + element.attr("src"));
                connection.connect();
                m_imageList.add(BitmapFactory.decodeStream(connection.getInputStream()));
                connection.disconnect();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    //MNHtmlからMNArticleの生成
    //生成できる条件が満たされていない場合nullが変える。
    MNArticle generateArticle()
    {
        MNArticle article = null;
        m_mainTitle = m_targetDoc.title();
        if(m_mainContents != null)
        {
            article = new MNArticle();
            article.setMainContents(m_mainContents);
            article.setMainTitle(m_mainTitle);
        }
        return article;
    }


}
