package com.uanid.toy.simplefileserver;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * @author uanid
 * @since 2020-01-24
 */
public class Utils {

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
