package com.github.dumper.common.transformer;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: huxing(xing.hu@me.com)
 * Date: 13-2-20
 * Time: 上午9:27
 *
 * 类目缓存对象
 */
public class CatCacheDo implements Serializable {
    private static final long serialVersionUID = 8212443179919876756L;

    /** 后台类目id */
    private String            cid;
    /** 前台类目id */
    private String            fid;
    /** 前台类目路径 */
    private String            catPath;
    /** 前台类目名称 */
    private String            name;
    /** 前台类目listname*/
    private String            listName;
    /** 前台类目listName，名称路径，格式如：369|苹果专区;1000000378|苹果主件 */
    private String            listNameCatNamePath;
    /** listName路径*/
    private String            listNamePath;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getCatPath() {
        return catPath;
    }

    public void setCatPath(String catPath) {
        this.catPath = catPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getListNameCatNamePath() {
        return listNameCatNamePath;
    }

    public void setListNameCatNamePath(String listNameCatNamePath) {
        this.listNameCatNamePath = listNameCatNamePath;
    }

    public String getListNamePath() {
        return listNamePath;
    }

    public void setListNamePath(String listNamePath) {
        this.listNamePath = listNamePath;
    }
}
