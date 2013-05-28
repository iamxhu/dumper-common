package com.github.dumper.common.writer;

import com.google.common.base.Strings;
import com.github.dumper.common.DumpType;
import com.github.dumper.common.FileWriter;
import com.github.dumper.configure.FileWriterConfigure;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.apache.velocity.texen.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: huxing(xing.hu@me.com)
 * Date: 13-2-19
 * Time: 下午2:25
 */
public class DefaultFileWriter implements FileWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFileWriter.class);

    private VelocityEngine      velocityEngine;
    private File                file;
    private FileWriterConfigure fileWriterConfigure;
    private Template            template;
    private VelocityContext     velocityContext;

    @Override
    public void init(FileWriterConfigure fileWriterConfigure, Map<String, String> velocityTools,
                     DumpType dumpType) {
        initVelocityEngine(fileWriterConfigure, velocityTools);
        this.fileWriterConfigure = fileWriterConfigure;

        file = createFile(fileWriterConfigure, dumpType);
        if (file.exists()) { //已存在，删除旧文件
            try {
                file.delete();
                file.createNewFile();
                LOGGER.info("创建新的数据文件，filename：" + file.getAbsolutePath());
            } catch (IOException e) {
                LOGGER.error("创建文件失败！", e);
            }
        }

        writeFileHeader();
    }

    /**
     * 假设设置的filePath为/opt/data，文件名为goodsData.xml 当前时间为2013/03/03 12:30
     * 全量文件名生成规则生成的文件全路径：/opt/data/full/201303031230_goodsData.xml
     * 增量文件名生成规则生成 的文件全路径：/opt/data/inc/20130303/1230_goodsData.xml
     * @param fileWriterConfigure fileWriterConfigure
     * @param dumpType dump 类型
     * @return 创建的文件
     */
    private File createFile(FileWriterConfigure fileWriterConfigure, DumpType dumpType) {
        String dateStr = DateFormatUtils.format(new Date(), "yyyyMMddHHmm");
        if (dumpType.equals(DumpType.FULL_DUMP)) {
            return new File(fileWriterConfigure.getFilePath() + "/full",
                dateStr + "_" + fileWriterConfigure.getFilename());
        } else {
            String dailyDir = DateFormatUtils.format(new Date(), "yyyyMMdd");
            String timeSuffix = DateFormatUtils.format(new Date(), "HHmm");
            String dir = fileWriterConfigure.getFilePath() + "/inc/" + dailyDir;
            File fileDir = new File(dir);
            if (!fileDir.exists()) {
                FileUtil.mkdir(dir);
            }
            return new File(dir, timeSuffix + "_" + fileWriterConfigure.getFilename());
        }
    }

    @Override
    public void writeFileHeader() {
        if (Strings.isNullOrEmpty(this.fileWriterConfigure.getFileHeader())) {
            return;
        }

        writeString(this.fileWriterConfigure.getFileHeader());
    }

    private void initVelocityEngine(FileWriterConfigure fileWriterConfigure,
                                    Map<String, String> velocityTools) {
        velocityEngine = new VelocityEngine();
        String vmPath = fileWriterConfigure.getVmPath();
        if (!Strings.isNullOrEmpty(vmPath)) {
            velocityEngine.addProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, vmPath);
        }

        velocityEngine
            .addProperty("file.resource.loader.class", FileResourceLoader.class.getName());
        velocityEngine.addProperty("input.encoding", "UTF-8");
        velocityEngine.addProperty("output.encoding", "UTF-8");
        velocityEngine.init();

        template = velocityEngine.getTemplate(fileWriterConfigure.getVmFilename());
        velocityContext = new VelocityContext();
        for (String key : velocityTools.keySet()) {
            String className = velocityTools.get(key);
            try {
                Class<?> clazz = this.getClass().getClassLoader().loadClass(className);
                Object tool = clazz.newInstance();
                velocityContext.put(key, tool);
            } catch (ClassNotFoundException e) {
                LOGGER.error("未找到VelocityTool中配置的类，请确认在当前classpath中存在该类。", e);
            } catch (InstantiationException e) {
                LOGGER.error("实例化出错,clazz:" + className, e);
            } catch (IllegalAccessException e) {
                LOGGER.error("实例化类时访问出错，clazz:" + className, e);
            }
        }
    }

    @Override
    public void writeData(List<Map<String, Object>> rows) {
        velocityContext.put("rows", rows);

        StringWriter stringWriter = new StringWriter();
        template.merge(velocityContext, stringWriter);

        writeString(stringWriter.toString());
    }

    private void writeString(String str) {
        RandomAccessFile writeFile = null;
        try {
            writeFile = new RandomAccessFile(file, "rw");
            writeFile.seek(writeFile.length());
            writeFile.write(str.getBytes());
        } catch (IOException e) {
            LOGGER.error("写入文件出错", e);
        } finally {
            if (writeFile != null) {
                try {
                    writeFile.close();
                } catch (IOException e) {
                    LOGGER.error("关闭文件异常。", e);
                }
            }
        }
    }

    @Override
    public void writeFooter() {
        if (Strings.isNullOrEmpty(this.fileWriterConfigure.getFileFooter())) {
            return;
        }

        writeString(this.fileWriterConfigure.getFileFooter());
    }
}
