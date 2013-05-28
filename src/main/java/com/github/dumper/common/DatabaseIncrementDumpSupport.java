package com.github.dumper.common;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by IntelliJ IDEA.
 * User: huxing(xing.hu@360hqb.com)
 * Date: 12-7-31
 * Time: 上午10:50
 */
public class DatabaseIncrementDumpSupport implements IncrementSupport {
    private static final Logger LOGGER = LoggerFactory
                                           .getLogger(DatabaseIncrementDumpSupport.class);
    private JdbcTemplate        jdbcTemplate;

    public DatabaseIncrementDumpSupport(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 获取本次增量的起始时间
     * 
     * @param dumpType
     * @return
     */
    public Date getStartDate(String dumpType) {
        if (dumpType == null || dumpType.trim() == "") {
            return null;
        }

        long lasttime = 0;
        try {
            lasttime = jdbcTemplate.queryForInt(DUMP_TIMER_SQL, new Object[] { dumpType });
        } catch (Exception e) {
            LOGGER.error("获取增量dump起始时间出错，增量类型：" + dumpType);
        }

        Date lastDate;
        if (lasttime != 0) {
            long millSeconds = lasttime * 1000;
            lastDate = new Date(millSeconds);
        } else {
            lastDate = new Date();
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("本次增量更新的起始时间点：" + lastDate.toString());
        }

        return lastDate;
    }

    /**
     * 增量完成后，设置当前时间为下一次的起始时间
     */
    public void updateLastTime(String dumpType) {
        long curSecond = System.currentTimeMillis() / 1000;
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("更新下一次增量的起始时间点：" + curSecond);
        }
        String sql = "update dump_timer set lasttime = " + curSecond + " where tbl='" + dumpType
                     + "'";
        jdbcTemplate.update(sql);
    }

    private static final String DUMP_TIMER_SQL = "select lasttime from dump_timer where tbl=?";
}
