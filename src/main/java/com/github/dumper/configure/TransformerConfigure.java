package com.github.dumper.configure;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Created by IntelliJ IDEA.
 * User: huxing(xing.hu@me.com)
 * Date: 13-2-20
 * Time: 上午11:50
 */
public class TransformerConfigure {
    private String              name;
    private String              className;
    private Map<String, String> properties      = new HashMap<String, String>();
    private Map<String, String> cacheConfigures = Maps.newHashMap();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Map getProperties() {
        return properties;
    }

    public void setProperties(Map properties) {
        this.properties = properties;
    }

    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    public Map<String, String> getCacheConfigures() {
        return cacheConfigures;
    }

    public void setCacheConfigures(Map<String, String> cacheConfigures) {
        this.cacheConfigures = cacheConfigures;
    }

    public void addCacheCinfigure(String key, String className) {
        this.cacheConfigures.put(key, className);
    }
}
