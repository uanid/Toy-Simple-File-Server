package com.uanid.toy.simplefileserver.driver;

import com.uanid.toy.simplefileserver.Utils;
import com.uanid.toy.simplefileserver.model.DownloadableFileMeta;
import com.uanid.toy.simplefileserver.model.FileMeta;
import com.uanid.toy.simplefileserver.model.InvalidPathException;
import lombok.RequiredArgsConstructor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author uanid
 * @since 2020-02-03
 */
@RequiredArgsConstructor
public class DiskFileSystemDriver implements AbstractFileDriver {

    private final String rootPathContext;

    private static final String[] WINDOWS_ILLEGAL_END_CHAR = {".", " "};
    private static final String[] WINDOWS_ILLEGAL_CHAR = {"<", ">", ":", "\"", "\\", "|", "?", "*"};
    private static final String[] WINDOWS_RESERVE_NAME = {"CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};
    private static final String HERE = ".";
    private static final String PREVIOUS = "..";

    public boolean isSecurePath(String path) {
        Function<String, Boolean> hasPathFunction = s -> path.contains("/" + s + "/") || path.endsWith("/" + s);

        boolean h1 = hasPathFunction.apply(HERE);
        boolean h2 = hasPathFunction.apply(PREVIOUS);
        return !(h1 || h2);
    }

    private boolean isIllegalPath(String path) {
        for (String s : WINDOWS_ILLEGAL_END_CHAR) {
            if (path.endsWith(s)) {
                return false;
            }
        }
        for (String s : WINDOWS_ILLEGAL_CHAR) {
            if (path.contains(s)) {
                return false;
            }
        }
        if (!path.equals("/")) {
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            int i1 = path.lastIndexOf('/');
            if (i1 == -1) {
                throw new IllegalArgumentException("Path cannot contains / " + path);
            } else {
                String lastItemName = path.substring(i1 + 1);
                for (String s : WINDOWS_RESERVE_NAME) {
                    if (lastItemName.contains(s)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public DownloadableFileMeta getFile(String path) throws InvalidPathException, FileNotFoundException {
        if (!isSecurePath(path)) {
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

    @Override
    public boolean delete(String path) throws InvalidPathException, FileNotFoundException {
        if (!isSecurePath(path)) {
            throw new InvalidPathException(path + " is insecure");
        }
        File fileOrDir = new File(rootPathContext, path);
        if (!fileOrDir.exists()) {
            throw new FileNotFoundException(path + " is not exists");
        }
        return fileOrDir.delete();
    }

    @Override
    public boolean createDirectory(String path) throws InvalidPathException {
        if (!isSecurePath(path)) {
            throw new InvalidPathException(path + " is insecure");
        }
        File file = new File(rootPathContext, path);
        if (file.isDirectory()) {
            throw new InvalidPathException(path + " is already exists");
        }
        return file.mkdirs();
    }

    @Override
    public void uploadFile(String path, String fileName, InputStream is) throws InvalidPathException {
        if (!isSecurePath(path)) {
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

    @Override
    public boolean isFile(String path) throws InvalidPathException {
        if (!isSecurePath(path)) {
            throw new InvalidPathException(path + " is insecure");
        }
        File file = new File(rootPathContext, path);
        if (!file.exists() || file.isDirectory()) {
            return false;
        }
        return file.isFile();
    }

    @Override
    public List<FileMeta> listingDir(String path) throws InvalidPathException, FileNotFoundException {
        if (!isSecurePath(path)) {
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
