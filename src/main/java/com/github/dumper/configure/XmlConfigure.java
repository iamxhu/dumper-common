package com.github.dumper.configure;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: huxing(xing.hu@me.com)
 * Date: 13-2-18
 * Time: 下午3:34
 */
public class XmlConfigure implements Configure {
    private static final Logger       LOGGER                           = LoggerFactory
                                                                           .getLogger(XmlConfigure.class);
    protected static final String     DATA_SOURCE                      = "dataSource";
    protected static final String     FILE_WRITER                      = "fileWriter";
    protected static final String     VELOCITY_TOOL                    = "velocityTool";
    protected static final String     TAG_PROPERTY                     = "property";
    public static final String        TAG_CACHE_SUPPORT                = "cacheSupport";
    public static final String        ENTITY_PROPERTY_KEY              = "key";
    public static final String        ENTITY_PROPERTY_NAME             = "name";
    public static final String        ENTITY_PROPERTY_QUERY            = "query";
    public static final String        ENTITY_PROPERTY_DRIVER_KEY       = "driverKey";
    public static final String        ENTITY_PROPERTY_DELTA_QUERY      = "deltaQuery";
    public static final String        ENTITY_CHILD_TAG_ENTITY          = "entity";
    public static final String        ENTITY_CHILD_TAG_TRANSFORMER     = "transformer";
    public static final String        TRANSFORMER_PROPERTY_CLASS       = "class";
    public static final String        TRANSFORMER_PROPERTY_NAME        = "name";
    public static final String        TRANSFORMER_CHILD_TAG_PROPERTY   = "property";
    public static final String        TRANSFORMER_CHILD_PROPERTY_KEY   = "key";
    public static final String        TRANSFORMER_CHILD_PROPERTY_VALUE = "value";
    public static final String        DATA_SOURCE_PROPERTY_DRIVER      = "driver";
    public static final String        DATA_SOURCE_PROPERTY_URL         = "url";
    public static final String        DATA_SOURCE_PROPERTY_USER        = "user";
    public static final String        DATA_SOURCE_PROPERTY_PASSWORD    = "password";
    public static final String        DATA_SOURCE_PROPERTY_MIN_IDLE    = "minIdle";
    public static final String        DATA_SOURCE_PROPERTY_MAX_IDLE    = "maxIdle";
    public static final String        DATA_SOURCE_PROPERTY_MAX_ACTIVE  = "maxActive";
    public static final String        CONFIGURE_FILE_NAME              = "dumper.xml";

    private DataSourceConfigure       dataSourceAttr;
    private Entity                    entity;
    private List<FileWriterConfigure> fileWriterConfigures;
    private Map<String, String>       velocityTools                    = new HashMap<String, String>();
    private List<CacheConfigure>      cacheConfigures                  = Lists.newArrayList();

    public XmlConfigure() throws ParserConfigurationException, IOException, SAXException {
        String path = System.getProperty("etc.home");
        InputStream stream = null;
        if (!Strings.isNullOrEmpty(path)) {
            stream = new FileSystemResource(path + File.separator + CONFIGURE_FILE_NAME)
                .getInputStream();
        }

        if (stream == null) {
            stream = this.getClass().getClassLoader().getResourceAsStream(CONFIGURE_FILE_NAME);
        }

        if (stream == null) {
            LOGGER.warn("没有找到数据dumper的配置文件dumper.xml。程序退出！etc.home:" + path );
            throw new IllegalArgumentException();
        }

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setCoalescing(true);
        DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(stream);
        dataSourceAttr = parseDataSource(document);

        Element documentElement = document.getDocumentElement();
        NodeList childNodes = documentElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeName().equalsIgnoreCase("entity")) {
                entity = parseEntity(item, null);
            }

        }

        NodeList fileWriterNodes = document.getElementsByTagName(FILE_WRITER);
        fileWriterConfigures = new ArrayList<FileWriterConfigure>(fileWriterNodes.getLength());
        for (int j = 0; j < fileWriterNodes.getLength(); j++) {
            fileWriterConfigures.add(parseFileWriter(fileWriterNodes.item(j)));
        }

        parseVelocityToolTag(document);

        parseCacheSupportTag(document);

    }

    private void parseCacheSupportTag(Document document) {
        NodeList cacheTag = document.getElementsByTagName(TAG_CACHE_SUPPORT);
        if (cacheTag != null && cacheTag.getLength() > 1) {
            throw new IllegalStateException("Have more than 1 " + TAG_CACHE_SUPPORT + " tags");
        }

        if (cacheTag != null && cacheTag.getLength() > 0) {
            Node cacheSupportNode = cacheTag.item(0);
            NodeList cacheNodes = cacheSupportNode.getChildNodes();
            for (int i = 0; i < cacheNodes.getLength(); i++) {
                Node cacheNode = cacheNodes.item(i);
                if (cacheNode.getNodeName().equalsIgnoreCase("cache")) {
                    NamedNodeMap cacheAttributes = cacheNode.getAttributes();
                    if (cacheAttributes == null) {
                        throw new IllegalStateException("Cache's don't have any properties.");
                    }

                    cacheConfigures.add(new CacheConfigure(getAttrValue(cacheAttributes, "name",
                        false), getAttrValue(cacheAttributes, "class", false)));
                }
            }
        }
    }

    private void parseVelocityToolTag(Document document) {
        NodeList velocityToolTag = document.getElementsByTagName(VELOCITY_TOOL);
        if (velocityToolTag != null && velocityToolTag.getLength() > 1) {
            throw new IllegalStateException("Have more than 1 " + VELOCITY_TOOL + " Tags.");
        }

        if (velocityToolTag != null && velocityToolTag.getLength() > 0) {
            Node velocityToolNode = velocityToolTag.item(0);
            NodeList velocityPropNodes = velocityToolNode.getChildNodes();
            for (int i = 0; i < velocityPropNodes.getLength(); i++) {
                Node velocityProp = velocityPropNodes.item(i);
                if (velocityProp.getNodeName().equalsIgnoreCase(TAG_PROPERTY)) {
                    NamedNodeMap velocityPropAttrs = velocityProp.getAttributes();
                    if (velocityPropAttrs == null) {
                        throw new IllegalStateException(
                            "VelocityTool's property tag doesn't have any properties.");
                    }

                    velocityTools.put(getAttrValue(velocityPropAttrs, "key", false),
                        getAttrValue(velocityPropAttrs, "class", false));
                }
            }
        }
    }

    private FileWriterConfigure parseFileWriter(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes == null) {
            throw new IllegalStateException("FileWriter don't have any attributes");
        }

        FileWriterConfigure fileWriterConfigure = new FileWriterConfigure();
        fileWriterConfigure.setClassName(getAttrValue(attributes,
            FileWriterConfigure.ATTRIBUTE_CLASS, false));
        fileWriterConfigure.setFilePath(getAttrValue(attributes,
            FileWriterConfigure.ATTRIBUTE_FILE_PATH, false));
        fileWriterConfigure.setVmPath(getAttrValue(attributes,
            FileWriterConfigure.ATTRIBUTE_VM_PATH, false));
        String name = getAttrValue(attributes, FileWriterConfigure.ATTRIBUTE_FILENAME, false);
        fileWriterConfigure.setFilename(name);
        fileWriterConfigure.setVmFilename(getAttrValue(attributes,
            FileWriterConfigure.ATTRIBUTE_VM_FILENAME, false));

        NodeList childNodes = node.getChildNodes();
        if (childNodes.getLength() > 0) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node item = childNodes.item(i);
                String header = getCharacterData(item, FileWriterConfigure.TAG_FILE_HEADER);
                if (header != null) {
                    fileWriterConfigure.setFileHeader(header);
                    ;
                }
                String footer = getCharacterData(item, FileWriterConfigure.TAG_FILE_FOOTER);
                if (footer != null) {
                    fileWriterConfigure.setFileFooter(footer);
                }

            }
        }
        return fileWriterConfigure;
    }

    private String getCharacterData(Node item, String tagName) {
        if (item.getNodeName().equalsIgnoreCase(tagName)) {
            Node cdata = item.getChildNodes().item(0);
            if ((CharacterData) cdata instanceof CharacterData) {
                return ((CharacterData) cdata).getData();
            }
        }

        return null;
    }

    private Entity parseEntity(Node node, Entity parentEntity) {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes == null) {
            throw new IllegalStateException("Entity don't have attributes");
        }

        Entity entity = new Entity();
        entity.setKey(getAttrValue(attributes, ENTITY_PROPERTY_KEY, true));
        entity.setName(getAttrValue(attributes, ENTITY_PROPERTY_NAME, false));
        entity.setQuery(getAttrValue(attributes, ENTITY_PROPERTY_QUERY, false));
        entity.setDriverKey(getAttrValue(attributes, ENTITY_PROPERTY_DRIVER_KEY, true));
        entity.setDeltaQuery(getAttrValue(attributes, ENTITY_PROPERTY_DELTA_QUERY, true));
        entity.setParentEntity(parentEntity);

        NodeList childNodes = node.getChildNodes();
        if (childNodes.getLength() > 0) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                if (childNode.getNodeName().equalsIgnoreCase(ENTITY_CHILD_TAG_ENTITY)) {
                    entity.addChildEntity(parseEntity(childNode, entity));
                }
                if (childNode.getNodeName().equalsIgnoreCase(ENTITY_CHILD_TAG_TRANSFORMER)) {
                    entity.setTransformerConfigure(getTransformerInstance(childNode));
                }
            }
        }

        return entity;
    }

    private TransformerConfigure getTransformerInstance(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes == null) {
            throw new IllegalStateException("Transformer don't have attributes");
        }

        String transformerClassName = getAttrValue(attributes, TRANSFORMER_PROPERTY_CLASS, false);

        TransformerConfigure transformerConfigure = new TransformerConfigure();
        transformerConfigure.setName(getAttrValue(attributes, TRANSFORMER_PROPERTY_NAME, false));
        transformerConfigure.setClassName(transformerClassName);
        NodeList childNodes = node.getChildNodes();
        if (childNodes.getLength() > 0) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                if (childNode.getNodeName().equalsIgnoreCase(TRANSFORMER_CHILD_TAG_PROPERTY)) {
                    NamedNodeMap propAttrs = childNode.getAttributes();
                    if (propAttrs == null) {
                        throw new IllegalStateException(
                            "Transformer's property don't have attributes");
                    }

                    transformerConfigure.addProperty(
                        getAttrValue(propAttrs, TRANSFORMER_CHILD_PROPERTY_KEY, false),
                        getAttrValue(propAttrs, TRANSFORMER_CHILD_PROPERTY_VALUE, false));
                }
            }
        }

        return transformerConfigure;
    }

    private DataSourceConfigure parseDataSource(Document document) {
        NodeList nodeList = document.getElementsByTagName(DATA_SOURCE);
        if (nodeList.getLength() != 1) {
            throw new IllegalStateException("datasource don't provide.");
        }

        Node dataSource = nodeList.item(0);
        if (dataSource == null) {
            throw new IllegalStateException("dataSource don't exist");
        }
        NamedNodeMap attributes = dataSource.getAttributes();
        if (attributes == null) {
            throw new IllegalStateException("dataSource don't provide attributes!");
        }
        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure();
        dataSourceConfigure.setDriver(getAttrValue(attributes, DATA_SOURCE_PROPERTY_DRIVER, false));
        dataSourceConfigure.setUrl(getAttrValue(attributes, DATA_SOURCE_PROPERTY_URL, false));
        dataSourceConfigure.setUser(getAttrValue(attributes, DATA_SOURCE_PROPERTY_USER, false));
        dataSourceConfigure.setPassword(getAttrValue(attributes, DATA_SOURCE_PROPERTY_PASSWORD,
            false));
        dataSourceConfigure
            .setMinIdle(getAttrValue(attributes, DATA_SOURCE_PROPERTY_MIN_IDLE, true));
        dataSourceConfigure
            .setMaxIdle(getAttrValue(attributes, DATA_SOURCE_PROPERTY_MAX_IDLE, true));
        dataSourceConfigure.setMaxActive(getAttrValue(attributes, DATA_SOURCE_PROPERTY_MAX_ACTIVE,
            true));

        return dataSourceConfigure;
    }

    private String getAttrValue(NamedNodeMap attributes, String attrName, boolean canNull) {
        Node attrNameNode = attributes.getNamedItem(attrName);
        if (attrNameNode == null && !canNull) {
            throw new IllegalStateException("tag's " + attrName + " don't provide!");
        }

        if (attrNameNode != null) {
            return attrNameNode.getNodeValue();
        }

        return null;
    }

    @Override
    public String getValue(String key) {
        return null;
    }

    @Override
    public DataSourceConfigure getDataSourceConfigure() {
        return dataSourceAttr;
    }

    public DataSourceConfigure getDataSourceAttr() {
        return dataSourceAttr;
    }

    public void setDataSourceAttr(DataSourceConfigure dataSourceAttr) {
        this.dataSourceAttr = dataSourceAttr;
    }

    public com.github.dumper.configure.Entity getEntity() {
        return entity;
    }

    public void setEntity(com.github.dumper.configure.Entity entity) {
        this.entity = entity;
    }

    public List<FileWriterConfigure> getFileWriterConfigures() {
        return fileWriterConfigures;
    }

    public void setFileWriterConfigures(List<FileWriterConfigure> fileWriterConfigures) {
        this.fileWriterConfigures = fileWriterConfigures;
    }

    public Map<String, String> getVelocityTools() {
        return velocityTools;
    }

    public void setVelocityTools(Map<String, String> velocityTools) {
        this.velocityTools = velocityTools;
    }

    public void addVelocityTool(String name, String className) {
        this.velocityTools.put(name, className);
    }

    public List<CacheConfigure> getCacheConfigures() {
        return cacheConfigures;
    }

    public static void main(String[] args) throws IOException, SAXException,
                                          ParserConfigurationException {
        XmlConfigure xmlConfigure = new XmlConfigure();
    }
}
