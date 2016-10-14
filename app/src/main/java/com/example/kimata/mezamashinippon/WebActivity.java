package com.example.kimata.mezamashinippon;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

/**
 * Created by kimata on 16/08/18.
 *
 * startArticle : 本文　先頭の文字列
 * endArticle : 本文　末尾の文字列
 * samplePageURL : 本文と末尾を取ってきた記事のURL
 * URL : リストに表示させるサイト名
 */
public class WebActivity extends Activity {

    public String startArticle = "";
    public String URL;
    public String samplePageURL;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_websearch);
        // トップページへ誘導するダイアログの表示

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("追加したいサイトのトップページへ移動して、下の追加ボタンを押してください")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();

        final WebView webView = (WebView) findViewById(R.id.websearchview);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://google.co.jp");

        //jacascriptを許可する
        webView.getSettings().setJavaScriptEnabled(true);
        webView.clearCache(true);
        webView.clearHistory();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_page);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // ページurlを取得
                URL = webView.getUrl();

                showSiteNameDialog();
                //showStartArticleDialog();
                //showEndArticleDialog();

                webView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        // WebView上でJavaScriptを動作させる
                        String script = "javascript:alert(document.getSelection().toString())";
                        webView.loadUrl(script);

                        // alert関数のmessage部分を使ってJS→Javaへ値を受け渡し
                        webView.setWebChromeClient(new WebChromeClient() {
                            @Override
                            public boolean onJsAlert(WebView view, String url, String message, android.webkit.JsResult result) {
                                try {
                                    if (startArticle.length() == 0) {
                                        startArticle = message;
                                        if (startArticle.length() != 0) {
                                            Log.d("debug", "startArticle：" + message);

                                            //サンプルページのURL取得
                                            samplePageURL =  webView.getUrl();
                                            Log.d("debug" , "samplePageURLは："+samplePageURL);

                                            // 遷移します
                                            Intent objIntent = new Intent(getApplicationContext(), SettingActivity.class);
                                            objIntent.putExtra("url", URL);
                                            objIntent.putExtra("startArticle", startArticle);
                                            objIntent.putExtra("samplePageURL", samplePageURL);
                                            startActivity(objIntent);
                                        }
                                    }
                                    return true;
                                } finally {
                                    result.confirm();
                                }
                            }
                        });

                        return false;
                    }
                });
            }
        });
    }

    // AlertDialogは入れ子にして実装する。ボタンを押したら次のダイアログへ

    // リストに表示するサイト名を入力するダイアログ
    private void showSiteNameDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editView = new EditText(getApplicationContext());
        editView.setText(URL);
        editView.setTextColor(Color.BLACK);
        builder.setMessage("サイトの登録名を入力してください")
                .setView(editView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // デフォルトでは入力フォームにurlが設定
                        URL = editView.getText().toString();
                        Log.d("debug", "リストに追加されるサイト名は:" + URL);

                        showStartArticleDialog();
                    }
                });
        builder.show();
    }

    // 文頭選択のアナウンスのダイアログ
    private void showStartArticleDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 先頭文字選択のアナウンス
        builder.setMessage("次にサイトの構造を登録します。\nまず、適当な記事の「先頭の文字列」を選択して長押ししてください。")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.show();
    }
}