package com.fuyong.main.test;

import com.fuyong.main.HttpDownload;
import com.fuyong.main.Log;
import com.fuyong.main.MyAppDirs;
import com.fuyong.main.MyTrafficStatsTask;
import org.dom4j.Element;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-8-5
 * Time: 下午8:22
 * To change this template use File | Settings | File Templates.
 */
public class HttpDownloadTest extends Test implements Observer {
    private List<HttpCfg> httpCfgList = new ArrayList<HttpCfg>();
    private int interval;
    private HttpDownload httpDownload;
    private int rxkpsZeroCount;

    public class HttpCfg {
        public String url;
        public int threadCount;
        public int count;
    }

    @Override
    public void config(Element element) {

        if (null == element) {
            return;
        }
        for (Iterator iter = element.elementIterator("row"); iter.hasNext(); ) {
            Element row = (Element) iter.next();
            HttpCfg httpCfg = new HttpCfg();
            httpCfg.url = getStringValue(row.elementTextTrim("url"), "");
            httpCfg.threadCount = Integer.parseInt(getStringValue(row.elementTextTrim("thread-count"), "1"));
            httpCfg.count = Integer.parseInt(getStringValue(row.elementTextTrim("count"), "0"));
            httpCfgList.add(httpCfg);
        }
        interval = Integer.parseInt(getStringValue(element.elementTextTrim("test-interval"), "5"));
    }

    @Override
    public Object call() throws Exception {
        log.info("begin http download test");
        MyTrafficStatsTask.getInstance().registerObserver(this);
        try {
            for (HttpCfg httpCfg : httpCfgList) {
                int count = httpCfg.count;
                for (int i = 0; i < count; ++i) {
                    rxkpsZeroCount = 0;
                    httpDownload = new HttpDownload(httpCfg.url,
                            MyAppDirs.getAppRootDir() + "down/", httpCfg.threadCount);
                    httpDownload.download(true);
                    log.info("file size: " + httpDownload.getFileSize() + "download size: " + httpDownload.downloadSize());
                    Thread.sleep(1000 * interval);
                }
            }
        } catch (InterruptedException e) {
            Log.exception(e);
        } catch (Exception e) {
            Log.exception(e);
        } finally {
            if (null != httpDownload) {
                httpDownload.stop();
            }
        }
        MyTrafficStatsTask.getInstance().unRegisterObserver(this);
        log.info("end http download test");
        return null;
    }

    @Override
    public void update(Observable observable, Object data) {
        if (MyTrafficStatsTask.getInstance().getUidRxkps() < 0.1) {
            ++rxkpsZeroCount;
            if (rxkpsZeroCount > 30) {
                httpDownload.stop();
                log.info("stop http download: continuously count of zero rate > 30");
            }
        } else {
            rxkpsZeroCount = 0;
        }
    }
}
