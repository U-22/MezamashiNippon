package com.example.kimata.mezamashinippon;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.util.ArrayList;

/**
 * Created by umino on 16/10/17.
 */

public class MNAsyncImageLoaderClass implements LoaderManager.LoaderCallbacks<ArrayList<Bitmap>>{
    private MNLoaderCallbacks m_loaderCallbacks;
    private Context m_context;
    private MNHtml m_html;

    public MNAsyncImageLoaderClass(MNLoaderCallbacks loaderCallbacks, Context context, MNHtml html)
    {
        m_loaderCallbacks = loaderCallbacks;
        m_context = context;
        m_html = html;
    }

    @Override
    public Loader<ArrayList<Bitmap>> onCreateLoader(int id, Bundle args)
    {
        return new MNAsyncImageLoader();
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Bitmap>> loader, ArrayList<Bitmap> bitmapList)
    {
        m_loaderCallbacks.MNAsyncImageLoaderCallbacks(bitmapList);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Bitmap>> loader)
    {
        
    }
}
