package com.github.dumper;

import com.github.dumper.common.DataDumper;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by IntelliJ IDEA.
 * User: huxing(xing.hu@me.com)
 * Date: 13-3-12
 * Time: 上午11:35
 */
public class DumperTest {

    @Test
    public void testFull() {
        try {
            new DataDumper("full");
        } catch (Exception e) {
            System.out.println(e);
            Assert.assertTrue(false);
        }

    }

    @Test
    public void testIncrement() {
        try {
            new DataDumper("increment");
        } catch (Exception e) {
            System.out.println(e);
            Assert.assertTrue(false);
        }
    }
}
