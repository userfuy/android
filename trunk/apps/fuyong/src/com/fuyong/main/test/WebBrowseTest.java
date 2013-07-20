package com.fuyong.main.test;

import android.graphics.Bitmap;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
        public String count;
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
            webBrowseCfg.count = getStringValue(row.elementTextTrim("count"), "0");
            webBrowseCfgList.add(webBrowseCfg);
        }
        interval = Integer.parseInt(getStringValue(element.elementTextTrim("test-interval"), "5"));
    }

    @Override
    public Object call() {
        try {
            log.info("begin web brows test");
            initWebView();
            for (WebBrowseCfg webBrowseCfg : webBrowseCfgList) {
                int count = Integer.parseInt(getStringValue(webBrowseCfg.count, "0"));
                for (int i = 0; i < count; ++i) {
                    MyWebView.getInstance().loadUrl(webBrowseCfg.url);
                    synchronized (WebBrowseTest.this) {
                        wait(60 * 1000);
                    }
                    MyWebView.getInstance().clearView();
                    Thread.sleep(1000 * interval);
                }
            }
        } catch (InterruptedException e) {
            log.info("test interrupted");
        } catch (Exception e) {
            log.error(e.toString());
        } finally {
            log.info("end web brows test");
        }
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
