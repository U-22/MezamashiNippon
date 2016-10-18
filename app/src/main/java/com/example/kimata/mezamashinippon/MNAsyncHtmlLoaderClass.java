package com.example.kimata.mezamashinippon;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.os.Bundle;
import android.support.v4.content.Loader;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by umino on 16/10/17.
 */

public class MNAsyncHtmlLoaderClass implements LoaderManager.LoaderCallbacks<ArrayList<MNHtml>> {
    private MNLoaderCallbacks m_loaderCallbacks;
    private MNSite m_site;
    private Context m_context;

    public MNAsyncHtmlLoaderClass(MNLoaderCallbacks loaderCallbacks, Context context, MNSite site)
    {
        m_loaderCallbacks = loaderCallbacks;
        m_context = context;
        m_site = site;
    }

    @Override
    public Loader<ArrayList<MNHtml>> onCreateLoader(int id, Bundle args)
    {
        return new MNAsyncHtmlLoader(m_context, m_site);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MNHtml>> loader, ArrayList<MNHtml> htmlList)
    {
        m_loaderCallbacks.MNAsyncHtmlLoaderCallbacks(htmlList);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MNHtml>> loader)
    {

    }

}
