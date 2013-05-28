package com.github.dumper.common;

import com.github.dumper.configure.TransformerConfigure;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: huxing(xing.hu@me.com)
 * Date: 13-2-19
 * Time: 上午10:53
 */
public interface Transformer {

    /**
     * 对查询出来的数据进行转换，如果该条数据转换成功返回true，如果转换不成功或该条数据不合法，返回false
     *
     * @param row
     * @param dumpContext
     * @return
     */
    public boolean transform(Map<String, Object> row, DumpContext dumpContext);

    public TransformerConfigure getTransformerConfigure();

    public void setTransformerConfigure(TransformerConfigure transformerConfigure);
}
