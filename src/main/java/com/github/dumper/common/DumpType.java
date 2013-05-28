package com.github.dumper.common;

/**
 * Created by IntelliJ IDEA.
 * User: huxing(xing.hu@me.com)
 * Date: 13-2-27
 * Time: 上午10:22
 */
public enum DumpType {

    FULL_DUMP("full"), INCREMENT_DUMP("increment");

    private final String dumpType;

    DumpType(String dumpType) {
        this.dumpType = dumpType;
    }

    public String getDumpType() {
        return dumpType;
    }

    public static DumpType getDumpTypeByName(String dumpType) {
        for (DumpType type : DumpType.values()) {
            if (dumpType.equals(type.getDumpType())) {
                return type;
            }
        }

        return null;
    }

}
