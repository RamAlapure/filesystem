package com.github.filesystem;

import com.github.filesystem.exception.FileSystemException;
import com.github.filesystem.model.Configuration;

import java.io.InputStream;
import java.util.List;

/**
 * The file system interface for file system operation specifications.
 *
 * @author Ram Alapure
 * @version 1.0
 * @since 12/02/2020
 */
public abstract class FileSystem {

    /**
     * The method used for the file system configuration. It configure aws S3 client based on the s3 credentials.
     *
     * @param config - The file system configuration {@link Configuration}.
     * @return Returns file system instance based on configuration provided.
     */
    public abstract FileSystem configure(Configuration config) throws FileSystemException;

    /**
     * This method can be used to get the file input stream for given input file path.
     *
     * @param filePath - The file path
     * @return Returns file input stream for given input file path.
     * @throws FileSystemException
     */
    public abstract InputStream read(String filePath) throws FileSystemException;

    /**
     * This method can be used to upload file input stream to file system.
     *
     * @param inputStream - The file input stream.
     * @param fileName    - The file name to save on file system.
     * @throws FileSystemException
     */
    public abstract void uploadFile(InputStream inputStream, String fileName) throws FileSystemException;

    /**
     * This method can be used to download the file from file system to specific destination path.
     *
     * @param source      - The source file name to download from file system.
     * @param destination - The destination file name to save on local system.
     * @throws FileSystemException
     */
    public abstract void downloadFile(String source, String destination) throws FileSystemException;

    /**
     * This method can be used to delete the file from file system.
     *
     * @param fileKey - The file name to delete.
     * @throws FileSystemException
     */
    public abstract void deleteFile(String fileKey) throws FileSystemException;

    /**
     * This method can be used to create the folder on file system.
     *
     * @param folderName - The folder name to create.
     * @throws FileSystemException
     */
    public abstract void createFolder(String folderName) throws FileSystemException;

    /**
     * This method can be used to copy a file from source to destination on file system.
     *
     * @param sourceFilePath - The source file path to copy from.
     * @param targetFilePath - The target file path to save on file system.
     * @throws FileSystemException
     */
    public abstract void copyFile(String sourceFilePath, String targetFilePath) throws FileSystemException;

    /**
     * This method can be used to get the configurations of file system.
     *
     * @return Returns the configurations {@link Configuration}
     */
    public abstract Configuration getConfiguration();

    /**
     * This method will return the list of files available in the given input directory.
     *
     * @param directory - The directory name from where to read the files.
     * @return Returns the list of files available in the given input directory.
     * @throws FileSystemException
     */
    public abstract List<String> getListOfFiles(String directory) throws FileSystemException;
}
