package com.example.kimata.mezamashinippon;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by umino on 16/08/11.
 */
public class MNArticle {
    private String m_mainContents;
    private String m_mainTitle;
    private ArrayList<Bitmap> m_imageList;

    //コンストラクタ
    MNArticle()
    {
        m_mainContents = null;
        m_mainTitle = null;
    }

    //setter
    void setMainContents(String mainContents)
    {
        if(!mainContents.isEmpty())
        {
            m_mainContents = mainContents;
        }
    }

    void setMainTitle(String mainTitle)
    {
        if(!mainTitle.isEmpty())
        {
            m_mainTitle = mainTitle;
        }
    }

    //getter
    String getMainContents()
    {
        return m_mainContents;
    }
    String getMainTitle()
    {
        return m_mainTitle;
    }
}
