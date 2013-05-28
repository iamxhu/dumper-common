package com.github.dumper;

import com.github.dumper.common.DataDumper;
import com.github.dumper.common.DumpType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: huxing(xing.hu@me.com)
 * Date: 13-3-11
 * Time: 上午9:58
 */
public class SearchIndexDumper {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchIndexDumper.class);
    public static void main(String[] args) {
        if (args == null || args.length < 1) {
            usage();
            return;
        }

        try {
            if (DumpType.getDumpTypeByName(args[0]) != null) {
                new DataDumper(args[0]);
            }
        } catch (ClassNotFoundException e) {
            LOGGER.error("class 未找到", e);
        } catch (IllegalAccessException e) {
            LOGGER.error("", e);
        } catch (InstantiationException e) {
            LOGGER.error("", e);
        }
    }

    private static void usage() {
        System.out.println("SearchIndexDumper usage:");
        System.out.println("full dump: java -Detc.home=/opt/dumper/etc -jar dumper-common.jar full");
        System.out.println("increment dump: java -Detc.home=/opt/dumper/etc -jar dumper-common.jar increment");
    }
}
