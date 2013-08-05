package com.fuyong.main.test;

import android.graphics.Bitmap;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.fuyong.main.Log;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-7-20
 * Time: 下午4:05
 * To change this template use File | Settings | File Templates.
 */
public class WebBrowseTest extends Test {
    private List<WebBrowseCfg> webBrowseCfgList = new ArrayList<WebBrowseCfg>();
    private int interval;

    public class WebBrowseCfg {
        public String url;
        public int count;
    }

    @Override
    public void config(Element element) {

        if (null == element) {
            return;
        }
        for (Iterator iter = element.elementIterator("row"); iter.hasNext(); ) {
            Element row = (Element) iter.next();
            WebBrowseCfg webBrowseCfg = new WebBrowseCfg();
            webBrowseCfg.url = getStringValue(row.elementTextTrim("url"), "");
            webBrowseCfg.count = Integer.parseInt(getStringValue(row.elementTextTrim("count"), "0"));
            webBrowseCfgList.add(webBrowseCfg);
        }
        interval = Integer.parseInt(getStringValue(element.elementTextTrim("test-interval"), "5"));
    }

    @Override
    public Object call() {
        log.info("begin web brows test");
        try {
            initWebView();
            for (WebBrowseCfg webBrowseCfg : webBrowseCfgList) {
                int count = webBrowseCfg.count;
                for (int i = 0; i < count; ++i) {
                    MyWebView.getInstance().loadUrl(webBrowseCfg.url);
                    synchronized (WebBrowseTest.this) {
                        wait(60 * 1000);
                    }
                    Thread.sleep(1000 * interval);
                }
            }
        } catch (InterruptedException e) {
            Log.exception(e);
        } catch (Exception e) {
            Log.exception(e);
        }
        log.info("end web brows test");
        return null;
    }

    private void stopWait() {
        synchronized (WebBrowseTest.this) {
            notifyAll();
        }
    }

    private void initWebView() {
        MyWebView.getInstance().setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });
        MyWebView.getInstance().setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;// false 显示frameset, true 不显示Frameset
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                stopWait();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                stopWait();
            }
        });
    }
}
