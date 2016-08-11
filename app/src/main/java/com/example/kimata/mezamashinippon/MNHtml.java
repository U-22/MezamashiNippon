package com.example.kimata.mezamashinippon;

/**
 * Created by umino on 16/08/11.
 */
import android.graphics.Bitmap;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
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

    //コンストラクタ
    MNHtml(String url)
    {
        m_targetUrl = url;

        try {
            m_targetDoc = Jsoup.connect(m_targetUrl).get();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
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



    //本文取得関数
    boolean findStartElement()
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
    }


}
