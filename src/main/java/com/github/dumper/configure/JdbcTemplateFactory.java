package com.github.dumper.configure;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by IntelliJ IDEA.
 * User: huxing(xing.hu@360hqb.com)
 * Date: 12-7-25
 * Time: 上午10:05
 */
public class JdbcTemplateFactory {
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public JdbcTemplateFactory(Configure configure) {
        DataSource dataSource = getDataSource(configure.getDataSourceConfigure());
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    protected DataSource getDataSource(DataSourceConfigure dataSourceConfigure) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setTestOnBorrow(true);
        dataSource.setDriverClassName(dataSourceConfigure.getDriver());
        dataSource.setUrl(dataSourceConfigure.getUrl());
        dataSource.setUsername(dataSourceConfigure.getUser());
        dataSource.setPassword(dataSourceConfigure.getPassword());
        dataSource.setMinIdle(Integer.parseInt(dataSourceConfigure.getMinIdle()));
        dataSource.setMaxIdle(Integer.parseInt(dataSourceConfigure.getMaxIdle()));
        dataSource.setMaxActive(Integer.parseInt(dataSourceConfigure.getMaxActive()));
        return dataSource;
    }
}
