package com.github.dumper.configure;

import java.util.ArrayList;
import java.util.List;

import com.github.dumper.common.Transformer;

/**
 * Created by IntelliJ IDEA.
 * User: huxing(xing.hu@me.com)
 * Date: 13-2-18
 * Time: 下午3:49
 */
public class Entity {
    private Entity               parentEntity;
    private List<Entity>         childEntity;
    private String               name;
    private String               query;
    private String               key;
    //root Entity 做SQL优化时的分页查询的主键key
    private String               driverKey;
    private Transformer          transformer;
    private TransformerConfigure transformerConfigure;
    //增量索引时获取数据的SQL
    private String               deltaQuery;

    public List<Entity> getChildEntity() {
        return childEntity;
    }

    public void setChildEntity(List<Entity> childEntity) {
        this.childEntity = childEntity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void addChildEntity(Entity entity) {
        if (getChildEntity() == null) {
            this.childEntity = new ArrayList<Entity>();
        }

        this.childEntity.add(entity);
    }

    public Entity getParentEntity() {
        return parentEntity;
    }

    public void setParentEntity(Entity parentEntity) {
        this.parentEntity = parentEntity;
    }

    public Transformer getTransformer() {
        return transformer;
    }

    public void setTransformer(Transformer transformer) {
        this.transformer = transformer;
    }

    public String getDriverKey() {
        return driverKey;
    }

    public void setDriverKey(String driverKey) {
        this.driverKey = driverKey;
    }

    public TransformerConfigure getTransformerConfigure() {
        return transformerConfigure;
    }

    public void setTransformerConfigure(TransformerConfigure transformerConfigure) {
        this.transformerConfigure = transformerConfigure;
    }

    public String getDeltaQuery() {
        return deltaQuery;
    }

    public void setDeltaQuery(String deltaQuery) {
        this.deltaQuery = deltaQuery;
    }
}
