package com.uanid.toy.simplefileserver;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/**
 * @author uanid
 * @since 2020-01-24
 */
public class Utils {
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

    public static String urlDecode(String url){
        try {
            return URLDecoder.decode(url, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("An error has occurred that cannot happen",e);
        }
    }
}
