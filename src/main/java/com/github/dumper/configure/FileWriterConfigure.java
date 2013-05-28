package com.github.dumper.configure;

/**
 * Created by IntelliJ IDEA.
 * User: huxing(xing.hu@me.com)
 * Date: 13-2-19
 * Time: 上午11:47
 */
public class FileWriterConfigure {
    public static final String ATTRIBUTE_CLASS       = "class";
    public static final String ATTRIBUTE_FILE_PATH   = "filePath";
    public static final String ATTRIBUTE_VM_PATH     = "vmPath";
    public static final String ATTRIBUTE_FILENAME    = "filename";
    public static final String ATTRIBUTE_VM_FILENAME = "vmFilename";
    public static final String TAG_FILE_FOOTER       = "footer";
    public static final String TAG_FILE_HEADER       = "header";
    /** 输出文件路径*/
    private String             filePath;
    /** 模板文件路径*/
    private String             vmPath;
    /** 模板名称*/
    private String             vmFilename;
    /** 输出文件名称*/
    private String             filename;
    /** FileWriter实现类的全名*/
    private String             className;
    /** 输出文件头信息*/
    private String             fileHeader;
    /** 输出文件的尾信息*/
    private String             fileFooter;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getVmPath() {
        return vmPath;
    }

    public void setVmPath(String vmPath) {
        this.vmPath = vmPath;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getVmFilename() {
        return vmFilename;
    }

    public void setVmFilename(String vmFilename) {
        this.vmFilename = vmFilename;
    }

    public String getFileHeader() {
        return fileHeader;
    }

    public void setFileHeader(String fileHeader) {
        this.fileHeader = fileHeader;
    }

    public String getFileFooter() {
        return fileFooter;
    }

    public void setFileFooter(String fileFooter) {
        this.fileFooter = fileFooter;
    }
}
