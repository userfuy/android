package com.fuyong.main.test;

import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.fuyong.main.MyApp;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-7-20
 * Time: 下午4:47
 * To change this template use File | Settings | File Templates.
 */
public class MyWebView {
    private static MyWebView instance;
    private WebView webView;

    private MyWebView() {
        webView = new WebView(MyApp.getInstance().getAppContext());
        webView.getSettings().setJavaScriptEnabled(true);// 可用JS
        webView.setScrollBarStyle(0);// 滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上
        webView.getSettings().setSupportZoom(true);// 支持缩放
        webView.getSettings().setBuiltInZoomControls(true);// 显示放大缩小
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
    }

    synchronized public static MyWebView getInstance() {
        if (null == instance) {
            instance = new MyWebView();
        }
        return instance;
    }

    public void setWebChromeClient(WebChromeClient client) {
        webView.setWebChromeClient(client);
    }

    public void setWebViewClient(WebViewClient client) {
        webView.setWebViewClient(client);
    }

    public void loadUrl(String url) {
        webView.loadUrl(url);
    }

    public void stopLoading() {
        webView.stopLoading();
    }

    public void clearView() {
        webView.clearView();
    }

    public WebView getWebView() {
        return webView;
    }

}
