package com.fuyong.main.test;

import com.fuyong.main.Log;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-7-20
 * Time: 上午11:05
 * To change this template use File | Settings | File Templates.
 */
public class TestConfig {
    private SAXReader reader = new SAXReader();
    private Document document;
    String filePath;
    List<Test> testList = new ArrayList<Test>();

    public List<Test> getTestList() {
        return testList;
    }

    public boolean load(String filePath) {
        if (null == filePath) {
            return false;
        }
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return false;
        }
        this.filePath = filePath;
        try {
            document = reader.read(file);
            return true;
        } catch (DocumentException e) {
            Log.getLogger(Log.MY_APP).error("parse xml error:" + filePath);
            return false;
        }
    }

    public void parse() {
        testList.clear();
        Element root = document.getRootElement();
        if (!root.getName().equals("test")) {
            return;
        }
        for (Iterator iter = root.elementIterator(); iter.hasNext(); ) {
            Element element = (Element) iter.next();
            if (element.getName().equals("voice")) {
                VoiceTest voiceTest = new VoiceTest();
                voiceTest.config(element);
                testList.add(voiceTest);
            } else if (element.getName().equals("web-browse")) {
                WebBrowseTest webBrowseTest = new WebBrowseTest();
                webBrowseTest.config(element);
                testList.add(webBrowseTest);
            } else if (element.getName().equals("http-download")) {
                HttpDownloadTest httpDownloadTest = new HttpDownloadTest();
                httpDownloadTest.config(element);
                testList.add(httpDownloadTest);
            }
        }
    }
}
