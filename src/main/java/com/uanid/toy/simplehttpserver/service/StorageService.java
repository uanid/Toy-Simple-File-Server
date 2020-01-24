package com.uanid.toy.simplehttpserver.service;

import com.uanid.toy.simplehttpserver.model.DownloadableFileMeta;
import com.uanid.toy.simplehttpserver.model.FileMeta;
import com.uanid.toy.simplehttpserver.model.InvalidPathException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author uanid
 * @since 2020-01-24
 */
@Service
@RequiredArgsConstructor
public class StorageService {

    private final ValidationService validationService;

    @Value("${rootPath}")
    private String rootPathContext;

    public synchronized DownloadableFileMeta getFile(String path) throws InvalidPathException, FileNotFoundException {
        if (!validationService.isSecurePath(path)) {
            throw new InvalidPathException(path + " is insecure");
        }

        File file = new File(rootPathContext, path);
        if (file.isDirectory()) {
            throw new InvalidPathException(path + " is not file");
        }
        if (!file.exists()) {
            throw new FileNotFoundException(path + " is not exists");
        }

        try {
            DownloadableFileMeta meta = new DownloadableFileMeta();
            meta.setIs(new FileInputStream(file));
            meta.setAbsolutePath(rootPathContext + path);
            meta.setDirectory(false);
            meta.setLength(file.length());
            meta.setRelatePath(path);
            meta.setName(file.getName());
            return meta;
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public synchronized boolean delete(String path) throws InvalidPathException, FileNotFoundException {
        if (!validationService.isSecurePath(path)) {
            throw new InvalidPathException(path + " is insecure");
        }
        File fileOrDir = new File(rootPathContext, path);
        if (!fileOrDir.exists()) {
            throw new FileNotFoundException(path + " is not exists");
        }
        return fileOrDir.delete();
    }

    public synchronized boolean createDirectory(String path) throws InvalidPathException {
        if (!validationService.isSecurePath(path)) {
            throw new InvalidPathException(path + " is insecure");
        }
        File file = new File(rootPathContext, path);
        if (file.isDirectory()) {
            throw new InvalidPathException(path + " is already exists");
        }
        return file.mkdirs();
    }

    public synchronized void uploadFile(String path, String fileName, InputStream is) throws InvalidPathException {
        if (!validationService.isSecurePath(path)) {
            throw new InvalidPathException(path + " is insecure");
        }
        File dir = new File(rootPathContext, path);
        if (!dir.isDirectory()) {
            throw new InvalidPathException(path + " is not directory or not exists");
        }

        File file = new File(dir, fileName);
        if (file.exists()) {
            throw new InvalidPathException(path + " is already exists");
        }

        try {
            FileOutputStream fos = new FileOutputStream(file);
            int r;
            byte[] buffer = new byte[8192];
            while ((r = is.read(buffer)) != -1) {
                fos.write(buffer, 0, r);
            }
            fos.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public boolean isFile(String path) throws InvalidPathException {
        if (!validationService.isSecurePath(path)) {
            throw new InvalidPathException(path + " is insecure");
        }
        File file = new File(rootPathContext, path);
        if (!file.exists() || file.isDirectory()) {
            return false;
        }
        return file.isFile();
    }

    public synchronized List<FileMeta> listingDir(String path) throws InvalidPathException, FileNotFoundException {
        if (!validationService.isSecurePath(path)) {
            throw new InvalidPathException(path + " is insecure");
        }

        File dir = new File(rootPathContext, path);
        if (!dir.isDirectory()) {
            throw new FileNotFoundException(path + " is not directory or not exists");
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return new ArrayList<>();
        } else {
            List<FileMeta> list = new ArrayList<>();
            for (File file : files) {
                FileMeta meta = new FileMeta()
                        .setAbsolutePath(dir.getAbsolutePath())
                        .setRelatePath(path)
                        .setName(file.getName())
                        .setDirectory(file.isDirectory())
                        .setLength(file.length());
                list.add(meta);
            }
            return list;
        }
    }
}
