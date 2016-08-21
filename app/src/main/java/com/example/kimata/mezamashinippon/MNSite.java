package com.example.kimata.mezamashinippon;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by umino on 16/08/19.
 */
public class MNSite {
    //m_urlはニュースサイトのトップページのURL
    //サイトの登録は、トップページのURL入力→パス生成用に任意の記事を表示して、始点と終点を選択の手順で行う
    private String m_url;
    private String m_rssUr;
    private String m_startIdentifier;
    private String m_endIdentifier;
    private ArrayList<String> m_startPath;
    private ArrayList<String> m_endPath;
    private ArrayList<String> m_newArticleList;

    MNSite(String url)
    {
        m_url = url;
        m_rssUr = new String();
        m_startIdentifier = new String();
        m_endIdentifier = new String();
        m_startPath = new ArrayList<String>();
        m_endPath = new ArrayList<String>();
        m_newArticleList = new ArrayList<String>();
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
    ArrayList<String> getStartPath()
    {
        return m_startPath;
    }
    ArrayList<String> getEndPath()
    {
        return m_endPath;
    }
    String getRssUrl()
    {
        return m_rssUr;
    }



    //サイトごとに固有のパスを設定
    void generateStartPath(final String sampleUrl)
    {
        if(!m_startIdentifier.isEmpty()) {
            try {
                Document targetDoc = Jsoup.connect(sampleUrl).get();
                Elements candinateElements = targetDoc.getElementsContainingOwnText(m_startIdentifier);
                Element startElement = candinateElements.first();
                while (true) {
                    startElement = startElement.parent();
                    if (startElement == null) {
                        break;
                    }
                    m_startPath.add(startElement.tagName());
                }
                Collections.reverse(m_startPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    void generateEndPath(final String sampleUrl)
    {
        if(m_endIdentifier != null)
        {
            try{
                Document targetDoc = Jsoup.connect(sampleUrl).get();
                Elements candinateElements = targetDoc.getElementsContainingOwnText(m_endIdentifier);
                Element endELement = candinateElements.last();
                //Element endOfDocument = targetDoc.getAllElements().last();
                while(true){
                    /*m_endPath.add(endOfDocument.tagName());
                    endOfDocument = endOfDocument.parent();*/
                    endELement = endELement.parent();
                    if(endELement == null)
                    {
                        break;
                    }
                    m_endPath.add(endELement.tagName());
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
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
    void addNewArticle()
    {
        
    }


    //MNHtmlの生成
    boolean generateHtml()
    {
        return true;
    }

}
