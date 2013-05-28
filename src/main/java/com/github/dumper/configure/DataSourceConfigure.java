package com.github.dumper.configure;

/**
 * Created by IntelliJ IDEA.
 * User: huxing(xing.hu@me.com)
 * Date: 13-2-18
 * Time: 下午5:24
 */
public class DataSourceConfigure {

    protected static final String DEFAULT_MIN_IDLE   = "5";
    protected static final String DEFAULT_MAX_IDLE   = "5";
    protected static final String DEFAULT_MAX_ACTIVE = "8";
    private String                driver;

    private String                url;
    private String                user;
    private String                password;
    private String                minIdle;
    private String                maxIdle;
    private String                maxActive;

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMinIdle() {
        if (minIdle == null || minIdle.trim() == "") {
            return DEFAULT_MIN_IDLE;
        }
        return minIdle;
    }

    public void setMinIdle(String minIdle) {
        this.minIdle = minIdle;
    }

    public String getMaxIdle() {
        if (maxIdle == null || maxIdle.trim() == "") {
            return DEFAULT_MAX_IDLE;
        }
        return maxIdle;
    }

    public void setMaxIdle(String maxIdle) {
        this.maxIdle = maxIdle;
    }

    public String getMaxActive() {
        if (maxActive == null || maxActive.trim() == "") {
            return DEFAULT_MAX_ACTIVE;
        }
        return maxActive;
    }

    public void setMaxActive(String maxActive) {
        this.maxActive = maxActive;
    }
}
