package com.uanid.toy.simplefileserver.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author uanid
 * @since 2020-01-24
 */
@Getter
@Setter
@Accessors(chain = true)
public class FileMeta {
    private boolean isDirectory;
    private String relatePath;
    private String absolutePath;
    private String name;
    private long length;
}
