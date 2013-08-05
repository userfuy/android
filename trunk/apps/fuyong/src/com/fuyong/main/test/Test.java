package com.fuyong.main.test;

import com.fuyong.main.Log;
import org.apache.log4j.Logger;
import org.dom4j.Element;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-6-23
 * Time: 下午4:38
 * To change this template use File | Settings | File Templates.
 */
public abstract class Test implements Callable<Object> {
    protected Logger log = Log.getLogger(Log.MY_APP);

    public abstract void config(Element element);

    protected String getStringValue(String value, String defaultValue) {
        if (null == value || value.isEmpty()) {
            return defaultValue;
        }
        return value;
    }
}
