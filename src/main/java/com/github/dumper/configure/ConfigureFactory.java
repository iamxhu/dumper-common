package com.github.dumper.configure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: huxing(xing.hu@360hqb.com)
 * Date: 12-7-25
 * Time: 上午10:08
 */
public class ConfigureFactory {
    private static final Logger    LOGGER   = LoggerFactory.getLogger(ConfigureFactory.class);

    public static final String     XML      = "xml";
    public static final String     PROP     = "prop";

    private Configure              configure;

    public static ConfigureFactory instance = new ConfigureFactory();

    private ConfigureFactory() {
    }

    public Configure getConfigure(String type) {
        if (type.equalsIgnoreCase("xml")) {
            try {
                configure = new XmlConfigure();
            } catch (ParserConfigurationException e) {
                LOGGER.error("解析xml配置文件发生异常", e);
            } catch (IOException e) {
                LOGGER.error("读取文件异常", e);
            } catch (SAXException e) {
                LOGGER.error("解析xml配置文件发生异常", e);
            }
        } else if (type.equalsIgnoreCase("prop")) {
            configure = new PropertiesConfigure();
        }

        return configure;
    }

}
