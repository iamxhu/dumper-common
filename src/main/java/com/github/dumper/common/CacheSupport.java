package com.github.dumper.common;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by IntelliJ IDEA.
 * User: huxing(xing.hu@me.com)
 * Date: 13-2-3
 * Time: 下午3:07
 */
public interface CacheSupport<K, V> {

    public V getValue(K k);

    public void init(JdbcTemplate jdbcTemplate);
}
