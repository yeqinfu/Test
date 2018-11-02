package com.llx278.uimocker2;

import android.annotation.SuppressLint;
import android.webkit.WebSettings;


/**
 *
 * Created by llx on 2018/3/13.
 */

class WebViewExecutor {

    WebViewExecutor() {
    }

    void executeJavaScript(final String url, Object webView) {
        final WebViewProxy proxy = WebViewProxyCreator.create(webView);
        Scheduler.runOnMainSync(new Runnable() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void run() {
                try {
                    Object settings = proxy.getSettings();
                    if (settings instanceof WebSettings) {
                        ((WebSettings) settings).setJavaScriptEnabled(true);
                    } else {
                        Reflect reflect = new Reflect(settings);
                        reflect.method("setJavaScriptEnabled",boolean.class).invoke(true);
                    }
                    proxy.loadUrl(url);
                } catch (Exception e) {
                    Logger.e(e);
                }
            }
        });
    }
}
