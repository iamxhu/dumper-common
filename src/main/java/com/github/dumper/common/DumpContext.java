package com.github.dumper.common;

import com.google.common.collect.Maps;
import com.github.dumper.configure.XmlConfigure;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: huxing(xing.hu@me.com)
 * Date: 13-2-20
 * Time: 上午11:03
 */
public class DumpContext {
    private JdbcTemplate              jdbcTemplate;
    private XmlConfigure              xmlConfigure;
    private List<FileWriter>          fileWriterList;
    private Map<String, CacheSupport> cacheMap = Maps.newHashMap();
    private DumpType                  dumpType;
    private IncrementSupport          incrementSupport;
    private String                    dumpName;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public XmlConfigure getXmlConfigure() {
        return xmlConfigure;
    }

    public void setXmlConfigure(XmlConfigure xmlConfigure) {
        this.xmlConfigure = xmlConfigure;
    }

    public List<FileWriter> getFileWriterList() {
        return fileWriterList;
    }

    public void setFileWriterList(List<FileWriter> fileWriterList) {
        this.fileWriterList = fileWriterList;
    }

    public Map<String, CacheSupport> getCacheMap() {
        return cacheMap;
    }

    public void setCacheMap(Map cacheMap) {
        this.cacheMap = cacheMap;
    }

    public void addCacheSupport(String cacheName, CacheSupport cacheSupport) {
        this.cacheMap.put(cacheName, cacheSupport);
    }

    public CacheSupport getCacheSupport(String cacheName) {
        return this.cacheMap.get(cacheName);
    }

    public DumpType getDumpType() {
        return dumpType;
    }

    public void setDumpType(DumpType dumpType) {
        this.dumpType = dumpType;
    }

    public IncrementSupport getIncrementSupport() {
        return incrementSupport;
    }

    public void setIncrementSupport(IncrementSupport incrementSupport) {
        this.incrementSupport = incrementSupport;
    }

    public String getDumpName() {
        return dumpName;
    }

    public void setDumpName(String dumpName) {
        this.dumpName = dumpName;
    }
}
