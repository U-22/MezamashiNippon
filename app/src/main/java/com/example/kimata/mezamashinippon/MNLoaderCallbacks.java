package com.example.kimata.mezamashinippon;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by umino on 16/10/17.
 */

public interface MNLoaderCallbacks {
    public void MNAsyncHtmlLoaderCallbacks(ArrayList<MNHtml> htmlList);
    public void MNAsyncImageLoaderCallbacks(Boolean result);
}
