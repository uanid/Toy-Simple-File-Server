package com.uanid.toy.simplefileserver.service;

import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * @author uanid
 * @since 2020-01-24
 */
@Service
public class ValidationService {

    private static final String HERE = ".";
    private static final String PREVIOUS = "..";

    public boolean isSecurePath(String path) {
        Function<String, Boolean> isNotSecure = s -> path.contains("/" + s + "/") || path.endsWith("/" + s);

        boolean h1 = isNotSecure.apply(HERE);
        boolean h2 = isNotSecure.apply(PREVIOUS);
        return !(h1 || h2);
    }
}
