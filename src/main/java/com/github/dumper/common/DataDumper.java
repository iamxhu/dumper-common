package com.github.dumper.common;

import com.github.dumper.configure.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: huxing(xing.hu@me.com)
 * Date: 13-2-19
 * Time: 上午10:08
 */
public class DataDumper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataDumper.class);

    public DataDumper(String type) throws ClassNotFoundException, IllegalAccessException,
                                  InstantiationException {
        Configure configure = ConfigureFactory.instance.getConfigure(ConfigureFactory.XML);
        XmlConfigure xmlConfigure = (XmlConfigure) configure;
        JdbcTemplateFactory jdbcTemplateFactory = new JdbcTemplateFactory(configure);
        JdbcTemplate jdbcTemplate = jdbcTemplateFactory.getJdbcTemplate();
        if (xmlConfigure.getFileWriterConfigures().size() == 0) {
            throw new IllegalStateException("Don't has any FileWriter.");
        }

        List<FileWriter> fileWriterList = new ArrayList<FileWriter>(xmlConfigure
            .getFileWriterConfigures().size());
        for (FileWriterConfigure fileWriterConfigure : xmlConfigure.getFileWriterConfigures()) {
            Class<FileWriter> fileWriterClass = (Class<FileWriter>) this.getClass()
                .getClassLoader().loadClass(fileWriterConfigure.getClassName());
            FileWriter writer = fileWriterClass.newInstance();
            writer.init(fileWriterConfigure, xmlConfigure.getVelocityTools(),
                DumpType.getDumpTypeByName(type));

            fileWriterList.add(writer);
        }

        DumpContext dumpContext = new DumpContext();
        dumpContext.setFileWriterList(fileWriterList);
        dumpContext.setJdbcTemplate(jdbcTemplate);
        dumpContext.setXmlConfigure(xmlConfigure);
        dumpContext.setDumpType(DumpType.getDumpTypeByName(type));
        dumpContext.setDumpName(xmlConfigure.getEntity().getName());
        if (xmlConfigure.getCacheConfigures().size() > 0) {
            for (CacheConfigure cacheConfigure : xmlConfigure.getCacheConfigures()) {
                Class<CacheSupport> cacheClazz = (Class<CacheSupport>) this.getClass()
                    .getClassLoader().loadClass(cacheConfigure.getCacheClass());
                CacheSupport cacheSupport = cacheClazz.newInstance();
                cacheSupport.init(jdbcTemplate);

                dumpContext.addCacheSupport(cacheConfigure.getCacheName(), cacheSupport);
            }
        }
        DatabaseIncrementDumpSupport dumpSupport = new DatabaseIncrementDumpSupport(jdbcTemplate);
        dumpContext.setIncrementSupport(dumpSupport);

        Entity entity = xmlConfigure.getEntity();
        initTransformer(entity);

        dump(dumpContext);
    }

    private void initTransformer(Entity entity) throws ClassNotFoundException,
                                               InstantiationException, IllegalAccessException {
        if (entity.getTransformerConfigure() != null) {
            TransformerConfigure transformerConfigure = entity.getTransformerConfigure();
            Class<Transformer> transformerClazz = (Class<Transformer>) this.getClass()
                .getClassLoader().loadClass(transformerConfigure.getClassName());
            Transformer transformer = transformerClazz.newInstance();
            transformer.setTransformerConfigure(transformerConfigure);
            entity.setTransformer(transformer);
        }

        if (entity.getChildEntity() != null && entity.getChildEntity().size() > 0) {
            for (Entity childEntity : entity.getChildEntity()) {
                initTransformer(childEntity);
            }
        }
    }

    private void dump(DumpContext dumpContext) {
        int pageSize = 500;
        Long startId = 1L;

        Entity entity = dumpContext.getXmlConfigure().getEntity();
        List<Map<String, Object>> mapList = getDataMaps(dumpContext, pageSize, startId, entity);
        while (CollectionUtils.isNotEmpty(mapList)) {
            Iterator iterator = mapList.iterator();

            while (iterator.hasNext()) {
                Map<String, Object> row = (Map<String, Object>) iterator.next();
                Transformer transformer = entity.getTransformer();
                if (transformer != null) {
                    boolean isOK = transformer.transform(row, dumpContext);
                    if (!isOK) {
                        iterator.remove();
                        continue;
                    }
                }
                boolean isOK = assemble(row, entity, dumpContext);
                if (!isOK) {
                    iterator.remove();
                }
                startId = Long.valueOf(String.valueOf(row.get(entity.getDriverKey())));
            }

            for (FileWriter fileWriter : dumpContext.getFileWriterList()) {
                fileWriter.writeData(mapList);
            }

            mapList = getDataMaps(dumpContext, pageSize, startId, entity);
        }

        for (FileWriter fileWriter : dumpContext.getFileWriterList()) {
            fileWriter.writeFooter();
        }

        if (dumpContext.getDumpType().equals(DumpType.INCREMENT_DUMP)) {
            dumpContext.getIncrementSupport().updateLastTime(dumpContext.getDumpName());
        }
    }

    private List<Map<String, Object>> getDataMaps(DumpContext dumpContext, int pageSize,
                                                  Long startId, Entity entity) {
        List<Map<String, Object>> mapList = null;
        Object[] queryParam;
        if (dumpContext.getDumpType().equals(DumpType.FULL_DUMP)) {
            queryParam = new Object[] { startId, pageSize };
            mapList = dumpContext.getJdbcTemplate().queryForList(entity.getQuery(), queryParam);
        } else if (dumpContext.getDumpType().equals(DumpType.INCREMENT_DUMP)) {
            queryParam = new Object[] {
                    dumpContext.getIncrementSupport().getStartDate(dumpContext.getDumpName()),
                    startId, pageSize };
            mapList = dumpContext.getJdbcTemplate()
                .queryForList(entity.getDeltaQuery(), queryParam);
        }
        return mapList;
    }

    private boolean assemble(Map<String, Object> stringObjectMap, Entity entity,
                          DumpContext dumpContext) {
        if (entity.getChildEntity() == null) {
            return true;
        }

        for (Entity childEntity : entity.getChildEntity()) {
            Map<String, Object> objectMap = null;
            try {
                objectMap = dumpContext.getJdbcTemplate().queryForMap(childEntity.getQuery(),
                    stringObjectMap.get(childEntity.getKey()));
                stringObjectMap.putAll(objectMap);
                Transformer transformer = childEntity.getTransformer();
                if (transformer != null) {
                    boolean isOK = transformer.transform(stringObjectMap, dumpContext);
                    if (!isOK) {
                        return false;
                    }
                }
                boolean isOK = assemble(stringObjectMap, childEntity, dumpContext);
                if (!isOK) {
                    return false;
                }
            } catch (Exception e) {
                LOGGER.error("出现异常");

//                if (e instanceof EmptyResultDataAccessException) {
//                    continue;
//                }
                return false;

            }
        }

        return true;
    }
}
