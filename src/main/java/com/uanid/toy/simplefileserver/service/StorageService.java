package com.uanid.toy.simplefileserver.service;

import com.uanid.toy.simplefileserver.driver.AbstractFileDriver;
import com.uanid.toy.simplefileserver.model.DownloadableFileMeta;
import com.uanid.toy.simplefileserver.model.FileMeta;
import com.uanid.toy.simplefileserver.model.InvalidPathException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * @author uanid
 * @since 2020-02-03
 */
@Service
@RequiredArgsConstructor
public class StorageService {

    private final AbstractFileDriver fileDriver;

    public DownloadableFileMeta getFile(String path) throws InvalidPathException, FileNotFoundException {
        return fileDriver.getFile(path);
    }

    public boolean delete(String path) throws InvalidPathException, FileNotFoundException {
        return fileDriver.delete(path);
    }

    public boolean createDirectory(String path) throws InvalidPathException {
        return fileDriver.createDirectory(path);
    }

    public void uploadFile(String path, String fileName, InputStream is) throws InvalidPathException {
        fileDriver.uploadFile(path, fileName, is);
    }

    public boolean isFile(String path) throws InvalidPathException {
        return fileDriver.isFile(path);
    }

    public List<FileMeta> listingDir(String path) throws InvalidPathException, FileNotFoundException {
        return fileDriver.listingDir(path);
    }
}
