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

public class MNHtml {
    private Document m_targetDoc;
    private String m_targetUrl;
    private String m_startIdentifier;
    private String m_endIdentifier;
    private Element m_startElement;
    private Element m_endElement;
    private String m_mainContents;
    private String m_mainTitle;
    private ArrayList<Bitmap> m_imageList;
    private ArrayList<String> m_startPath;
    private ArrayList<String> m_endPath;

    //コンストラクタ
    MNHtml(String url, ArrayList<String> path)
    {
        m_targetUrl = url;

        try {
            m_targetDoc = Jsoup.connect(m_targetUrl).get();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        m_imageList = new ArrayList<Bitmap>();
        m_startPath = new ArrayList<String>(path);
        //m_endPath = new ArrayList<String>();
        //m_startDepth = 0;
        //m_endDepth = 0;
        m_startIdentifier = new String();
        m_endIdentifier = new String();
        m_mainContents = new String();
        m_mainTitle = new String();
        m_startElement = null;
        m_endElement = null;
    }

    //setter
    void setStartIdentifier(String start)
    {
        if(!start.isEmpty())
        {
            m_startIdentifier = start;
        }
    }
    void setEndIdentifier(String end)
    {
        if(!end.isEmpty())
        {
            m_endIdentifier = end;
        }
    }

    //getter
    //for test
    String getStartElementString()
    {
        return m_startElement.ownText();
    }
    //for test
    String getEndELementString()
    {
        return m_endElement.ownText();
    }
    String getMainContents() {return m_mainContents;}



    //本文取得関数群
    //旧バージョン
    /*boolean findStartElement()
    {
        if(!m_startIdentifier.isEmpty()) {
            Elements candinateElements = m_targetDoc.getElementsContainingOwnText(m_startIdentifier);
            //先頭の要素をスタートノードとみなす
            m_startElement = candinateElements.first();
            return true;
        }else{
            return false;
        }
    }
    boolean findEndElement()
    {
        if(!m_endIdentifier.isEmpty()) {
            Elements candinateElements = m_targetDoc.getElementsContainingOwnText(m_endIdentifier);
            //最後の要素をエンドノードとみなす
            m_endElement = candinateElements.last();
            return true;
        }else{
            return false;
        }
    }*/
    //パスを使う新バージョン
    void findStartElement()
    {
        Elements candinates = m_targetDoc.getAllElements();
        for (String tagName : m_startPath)
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
    }
    void findEndElement()
    {
        Elements candinates = m_targetDoc.getAllElements();
        for (String tagName : m_startPath)
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
    }
    //箇条書きなど、htmlのスタイルによっては順番が前後することあり
    //要改良
    boolean generateMainContents()
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
    }

    //startElementまでの深さを保存
    void saveStartDepth()
    {
        if(m_startElement != null)
        {
            Element temp = m_startElement;
            while(true) {
                temp = temp.parent();
                if(temp == null)
                {
                    break;
                }
                Log.d("parent", "saveStartDepth: " + temp.tagName());
            }
        }
    }

    //endElementからDOM構造の終端までの深さを保存
    /*void saveEndDepth()
    {
        boolean startFlag = false;
        if(m_endElement != null)
        {
            Elements elements = m_targetDoc.getAllElements();
            for(Element element : elements)
            {
                if(element.equals(m_endElement))
                {
                    startFlag = true;
                }
                if(startFlag)
                {
                    m_endDepth++;
                }
            }
        }
        Log.d("edepth", "saveEndDepth: " + m_endDepth);
    }*/

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
