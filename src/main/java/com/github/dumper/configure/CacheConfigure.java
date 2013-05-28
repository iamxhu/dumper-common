package com.github.dumper.configure;

/**
 * Created by IntelliJ IDEA.
 * User: huxing(xing.hu@me.com)
 * Date: 13-2-27
 * Time: 上午9:43
 */
public class CacheConfigure {

    private String cacheName;

    private String cacheClass;

    public CacheConfigure(String cacheName, String cacheClass) {
        this.cacheName = cacheName;
        this.cacheClass = cacheClass;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public String getCacheClass() {
        return cacheClass;
    }

    public void setCacheClass(String cacheClass) {
        this.cacheClass = cacheClass;
    }
}
