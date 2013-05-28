package com.github.dumper.configure;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;

/**
 * Created by IntelliJ IDEA.
 * User: huxing(xing.hu@360hqb.com)
 * Date: 12-7-25
 * Time: 上午10:13
 */
public class PropertiesConfigure implements Configure {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesConfigure.class);
    private Properties          properties;

    @Override
    public String getValue(String key) {
        return String.valueOf(properties.get(key));
    }

    @Override
    public DataSourceConfigure getDataSourceConfigure() {
        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure();
        dataSourceConfigure.setDriver(properties.getProperty("importer.db.driver"));
        dataSourceConfigure.setUrl(properties.getProperty("importer.db.url"));
        dataSourceConfigure.setUser(properties.getProperty("importer.db.username"));
        dataSourceConfigure.setPassword(properties.getProperty("importer.db.password"));
        dataSourceConfigure.setMinIdle(properties.getProperty("importer.db.min.idle"));
        dataSourceConfigure.setMaxIdle(properties.getProperty("importer.db.max.idle"));
        dataSourceConfigure.setMaxActive(properties.getProperty("importer.db.max.active"));
        return dataSourceConfigure;
    }

    public PropertiesConfigure() {
        properties = new Properties();
        InputStream stream = this.getClass().getClassLoader()
            .getResourceAsStream("importer.properties");
        if (stream == null) {
            String path = System.getProperty("etc.home");
            try {
                stream = new FileSystemResource(path + File.separator + "importer.properties")
                    .getInputStream();
                properties.load(stream);
            } catch (IOException e) {
                LOGGER.error("读入配置文件出错！\n" + e.getMessage());
            }
        }
    }
}
