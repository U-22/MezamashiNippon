package com.example.kimata.mezamashinippon;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;

/**
 *　チュートリアルダイアログのアダプタ
 * Created by kimata on 2016/10/19.
 */

public class DialogPagerAdapter extends PagerAdapter {
    private Context mContext;
    private ArrayList<Integer> mList;
    private LayoutInflater inflater = null;

    public DialogPagerAdapter(Context context, ArrayList<Integer> list) {
        mContext = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mList = list;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // リストから取得
        if (position == 0) {
            FrameLayout view = (FrameLayout) inflater.inflate(R.layout.page0,null);
            // コンテナに追加
            container.addView(view);
            return view;

        } else if (position == 1) {
            FrameLayout view = (FrameLayout) inflater.inflate(R.layout.page1, null);
            // コンテナに追加
            container.addView(view);
            return view;
        } else if (position == 2) {
            FrameLayout view = (FrameLayout) inflater.inflate(R.layout.page2, null);
            // コンテナに追加
            container.addView(view);
            return view;
        } else if (position == 3) {
            FrameLayout view = (FrameLayout) inflater.inflate(R.layout.page3, null);
            // コンテナに追加
            container.addView(view);
            return view;

        } else if (position == 4) {
            FrameLayout view = (FrameLayout) inflater.inflate(R.layout.page4, null);
            // コンテナに追加
            container.addView(view);
            return view;
        }

        return false;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // コンテナから View を削除
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        // リストのアイテム数を返す
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        // Object 内に View が存在するか判定する
        return view == object;
    }
}
