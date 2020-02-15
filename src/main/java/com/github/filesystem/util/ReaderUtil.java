package com.github.filesystem.util;

import com.github.filesystem.FileSystem;
import com.github.filesystem.exception.FileSystemException;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * The AWS S3 client to perform operations on bucket.
 *
 * @author Ram Alapure
 * @version 1.0
 * @since 12/02/2020
 */
public final class ReaderUtil {

    private ReaderUtil() {
    }

    public static InputStream read(FileSystem fileSystem, String fileName) throws FileSystemException {
        ValidationUtil.rejectNull(fileName, "FileName");
        return fileSystem.read(fileName);
    }

    public static InputStream read(InputStream inputStream, String fileName) throws FileSystemException {
        try {
            ZipInputStream stream = new ZipInputStream(inputStream);
            ZipEntry entry;
            while ((entry = stream.getNextEntry()) != null) {
                if (fileName.equals(entry.getName())) {
                    return stream;
                }
            }
        } catch (IOException e) {
            ErrorUtil.fileSystemException(ExceptionConstants.STR_IO_EXCEPTION, e);
        }
        throw new FileSystemException(String.format(ExceptionConstants.STR_FILE_EXCEPTION, fileName));
    }

}
