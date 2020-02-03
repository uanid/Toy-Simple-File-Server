package com.uanid.toy.simplefileserver.driver;

import com.uanid.toy.simplefileserver.model.DownloadableFileMeta;
import com.uanid.toy.simplefileserver.model.FileMeta;
import com.uanid.toy.simplefileserver.model.InvalidPathException;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * @author uanid
 * @since 2020-02-03
 */
public interface AbstractFileDriver {

    DownloadableFileMeta getFile(String path) throws InvalidPathException, FileNotFoundException;

    boolean delete(String path) throws InvalidPathException, FileNotFoundException;

    boolean createDirectory(String path) throws InvalidPathException;

    void uploadFile(String path, String fileName, InputStream is) throws InvalidPathException;

    boolean isFile(String path) throws InvalidPathException;

    List<FileMeta> listingDir(String path) throws InvalidPathException, FileNotFoundException;
}
