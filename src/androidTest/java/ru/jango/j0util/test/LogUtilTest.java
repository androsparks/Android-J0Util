package ru.jango.j0util.test;

import junit.framework.Assert;
import junit.framework.TestCase;

import ru.jango.j0util.LogUtil;

public class LogUtilTest extends TestCase {

    private LogUtilWrapper luw;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        luw = new LogUtilWrapper();
    }

    public void testLogTagChange() throws Exception {
        LogUtil.LOG_TAG = "test_log_tag";
        LogUtil.logMemoryUsage();
        LogUtil.d(LogUtilTest.class, "test message");
    }

    public void testFormatNumber() throws Exception {
        Assert.assertEquals("0", luw.formatNumber2(0));
        Assert.assertEquals("999", luw.formatNumber2(999));
        Assert.assertEquals("1.000", luw.formatNumber2(1000));
        Assert.assertEquals("999.000", luw.formatNumber2(999000));
        Assert.assertEquals("1.000.000", luw.formatNumber2(1000000));
        Assert.assertEquals("1.000.000.000.000", luw.formatNumber2(1000000000000L));
    }

    private class LogUtilWrapper extends LogUtil {
        public String formatNumber2(long n) {
            return formatNumber(n);
        }
    }

}
