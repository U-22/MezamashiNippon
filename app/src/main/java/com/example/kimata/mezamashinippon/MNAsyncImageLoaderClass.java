package com.example.kimata.mezamashinippon;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

/**
 * Created by umino on 16/10/17.
 */

public class MNAsyncImageLoaderClass implements LoaderManager.LoaderCallbacks<Boolean>{
    private MNLoaderCallbacks m_loaderCallbacks;
    private Context m_context;
    private MNHtml m_html;

    public MNAsyncImageLoaderClass(MNLoaderCallbacks loaderCallbacks, Context context, MNHtml html)
    {
        m_loaderCallbacks = loaderCallbacks;
        m_context = context;
        m_html = html;
        Log.d("init", "MNAsyncImageLoaderClass: コンストラクタ終了");
    }

    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args)
    {
        Log.d("imageloader", "onCreateLoader: ");
        return new MNAsyncImageLoader(m_context, m_html);
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean result)
    {
        m_loaderCallbacks.MNAsyncImageLoaderCallbacks(result);
        Log.d("imageLoader", "onLoadFinished: ");
    }

    @Override
    public void onLoaderReset(Loader<Boolean> loader)
    {

    }
}
