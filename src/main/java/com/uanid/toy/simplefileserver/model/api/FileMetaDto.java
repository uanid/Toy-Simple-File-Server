package com.uanid.toy.simplefileserver.model.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author uanid
 * @since 2020-04-16
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileMetaDto {
    private boolean isDirectory;
    private String relatePath;
    private String name;
    private long length;
}
