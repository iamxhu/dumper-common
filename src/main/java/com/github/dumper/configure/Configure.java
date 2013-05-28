package com.github.dumper.configure;

/**
 * Created by IntelliJ IDEA.
 * User: huxing(xing.hu@360hqb.com)
 * Date: 12-7-25
 * Time: 上午10:10
 */
public interface Configure {

    public String getValue(String key);

    public DataSourceConfigure getDataSourceConfigure();

}
