package com.example.kimata.mezamashinippon;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
//import java.util.Objects;

/**
 * Created by umino on 16/10/09.
 */
public class MNArticleImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Bitmap> m_imageList;

    public MNArticleImageAdapter(Context c, ArrayList<Bitmap> imageList) {
        mContext = c;
        m_imageList = imageList;
    }

    public int getCount()
    {
        return m_imageList.size();
    }

    public Object getItem(int positon)
    {
        return null;
    }

    public long getItemId(int position)
    {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ImageView imageView;
        if(convertView == null)
        {
            imageView = new ImageView(mContext);
            imageView.setPadding(10, 10, 10, 10);
            imageView.setLayoutParams(new GridView.LayoutParams(700, 700));
        }else{
            imageView = (ImageView)convertView;
        }
        imageView.setImageBitmap(m_imageList.get(position));
        return imageView;
    }
}

