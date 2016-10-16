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
        m_mainTitle = m_targetDoc.title();
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
        String regex = "<strong(\"[^\"]*\"|'[^']*'|[^'\">])*>|</strong>|<a(\"[^\"]*\"|'[^']*'|[^'\">])*>|</a>|<b(\"[^\"]*\"|'[^']*'|[^'\">])*>|</b>";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        html = matcher.replaceAll("");
        //html文字列からdocumentを生成
        Document replacedDoc = Jsoup.parse(html);
        String content = new String();
        Elements candinates;
        /*if(m_startClassName != null && !m_startClassName.isEmpty())
        {
            candinates = replacedDoc.getElementsByClass(m_startClassName);
        }else*/
        if(m_startClassNameParent != null && !m_startClassNameParent.isEmpty())
        {
            Elements matchedElements = replacedDoc.getElementsByClass(m_startClassNameParent);
            if(matchedElements.size() == 0)
            {
                return;
            }
            Element matchedElementFirst = matchedElements.first();
            if(matchedElementFirst == null)
            {
                return;
            }
            candinates = matchedElementFirst.children();
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
    //http://からはじまっていないものは除去
    boolean getPicture()
    {
        Elements imageElement;
        if(m_startClassNameParent != null && !m_startClassNameParent.isEmpty())
        {
            Elements matchedElements = m_targetDoc.getElementsByClass(m_startClassNameParent);
            if(matchedElements.size() == 0)
            {
                return false;
            }
            Element matchedElementsFirst = matchedElements.first();
            if(matchedElementsFirst == null)
            {
                return false;
            }
            imageElement = matchedElementsFirst.getElementsByTag("img");
        }else{
            return false;
        }
        for(final Element element : imageElement)
        {
            try {
                if(element.attr("src").length() < 6)
                {
                    continue;
                }
                if(!element.attr("src").substring(0, 4).equals("http"))
                {
                    continue;
                }
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
        return  true;
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
