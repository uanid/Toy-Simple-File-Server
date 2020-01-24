package com.uanid.toy.simplefileserver.model;

import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

/**
 * @author uanid
 * @since 2020-01-24
 */
@Getter
@Setter
public class DownloadableFileMeta extends FileMeta {
    private InputStream is;
}
