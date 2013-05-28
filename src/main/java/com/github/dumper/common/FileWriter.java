package com.github.dumper.common;

import com.github.dumper.configure.FileWriterConfigure;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: huxing(xing.hu@me.com)
 * Date: 13-2-19
 * Time: 上午11:29
 */
public interface FileWriter {

    public void init(FileWriterConfigure fileWriterConfigure, Map<String, String> velocityTools,
                     DumpType dumpType);

    /** 写入文件头信息 */
    public void writeFileHeader();

    /** 写入dump出来的数据*/
    public void writeData(List<Map<String, Object>> rows);

    /**写入文件的结尾信息*/
    public void writeFooter();
}
