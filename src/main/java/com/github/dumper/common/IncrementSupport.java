package com.github.dumper.common;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: huxing(xing.hu@me.com)
 * Date: 13-2-27
 * Time: 上午10:33
 */
public interface IncrementSupport {

    /**
     * 根据增量类型，获取起始时间
     *
     * @param dumpType 增量类型
     * @return 时间
     */
    public Date getStartDate(String dumpType);

    /**
     * 根据增量类型，更新当前dump完成时间
     *
     * @param dumpType 增量数据导出类型
     */
    public void updateLastTime(String dumpType);
}
