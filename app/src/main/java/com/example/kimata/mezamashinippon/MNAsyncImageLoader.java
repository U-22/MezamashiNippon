package com.example.kimata.mezamashinippon;
import android.graphics.Bitmap;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;


/**
 * Created by umino on 16/10/17.
 */

public class MNAsyncImageLoader extends AsyncTaskLoader<Boolean>{
    private MNHtml m_html;

    public MNAsyncImageLoader(Context context, MNHtml html)
    {
        super(context);
        m_html = html;
    }

    @Override
    public Boolean loadInBackground(){
        return m_html.getPicture();
    }

    @Override
    protected void onStartLoading() {forceLoad();}
}
