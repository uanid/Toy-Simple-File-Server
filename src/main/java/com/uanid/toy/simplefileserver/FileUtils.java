package com.uanid.toy.simplefileserver;

import java.util.function.Function;

/**
 * @author uanid
 * @since 2020-01-24
 */
public class FileUtils {
    private static final String HERE = ".";
    private static final String PREVIOUS = "..";

    public static boolean isSecurePath(String path) {
        Function<String, Boolean> hasPathFunction = s -> path.contains("/" + s + "/") || path.endsWith("/" + s);

        boolean h1 = hasPathFunction.apply(HERE);
        boolean h2 = hasPathFunction.apply(PREVIOUS);
        return !(h1 || h2);
    }

    public static String byteCountToDisplaySize(long size) {
        return org.apache.commons.io.FileUtils.byteCountToDisplaySize(size);
    }
}
