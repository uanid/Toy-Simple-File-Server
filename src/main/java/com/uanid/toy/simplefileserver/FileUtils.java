package com.uanid.toy.simplefileserver;

/**
 * @author uanid
 * @since 2020-01-24
 */
public class FileUtils {
    public static String byteCountToDisplaySize(long size) {
        return org.apache.commons.io.FileUtils.byteCountToDisplaySize(size);
    }
}
