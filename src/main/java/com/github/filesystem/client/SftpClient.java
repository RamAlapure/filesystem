package com.github.filesystem.client;

import com.github.filesystem.FileSystem;
import com.github.filesystem.annotation.Component;
import com.github.filesystem.exception.FileSystemException;
import com.github.filesystem.model.Configuration;
import com.github.filesystem.util.AppConstants;
import com.github.filesystem.util.ErrorUtil;
import com.github.filesystem.util.ExceptionConstants;
import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * The SFTP client to perform operations on directory or file.
 *
 * @author Ram Alapure
 * @version 1.0
 * @since 12/02/2020
 */
@Getter
@NoArgsConstructor
@Component(AppConstants.STR_SFTP)
public class SftpClient extends FileSystem {

    public static final Logger log = Logger.getLogger(SftpClient.class.getName());

    private ChannelSftp sftpChannel;
    private Configuration config;

    /**
     * The method used for the file system configuration. It configure sftp client based on the sftp credentials.
     *
     * @param config
     * @return
     */
    public FileSystem configure(Configuration config) throws FileSystemException {
        log.info("Configuring the SFTP client.");
        this.config = config;
        Map<String, Object> properties = config.getProperties();
        JSch jsch = new JSch();
        try {
            Session session = jsch.getSession((String) properties.get(AppConstants.SFTP_USERNAME),
                    (String) properties.get(AppConstants.SFTP_HOSTNAME),
                    (int) properties.get(AppConstants.SFTP_PORT));
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword((String) properties.get(AppConstants.SFTP_PASSWORD));
            session.connect();
            Channel channel = session.openChannel(AppConstants.STR_SFTP.toLowerCase());
            channel.connect();
            sftpChannel = (ChannelSftp) channel;
        } catch (JSchException e) {
            ErrorUtil.fileSystemException(ExceptionConstants.STR_SFTP_EXCEPTION, e);
        }
        return this;
    }

    /**
     * This method can be used to get the file input stream for given input file path.
     *
     * @param filePath - The file path
     * @return Returns file input stream for given input file path.
     * @throws FileSystemException
     */
    @Override
    public InputStream read(String filePath) throws FileSystemException {
        InputStream inputStream = null;
        try {
            inputStream = sftpChannel.get(filePath);
        } catch (SftpException e) {
            ErrorUtil.fileSystemException(ExceptionConstants.STR_SFTP_EXCEPTION, e);
        }
        return inputStream;
    }

    /**
     * This method can be used to upload file input stream to file system.
     *
     * @param inputStream - The file input stream.
     * @param fileName    - The file name to save on file system.
     * @throws FileSystemException
     */
    @Override
    public void uploadFile(InputStream inputStream, String fileName) throws FileSystemException {
        log.info("Received request for uploading file to sftp.");
        try {
            sftpChannel.put(inputStream, fileName);
        } catch (SftpException e) {
            ErrorUtil.fileSystemException(ExceptionConstants.STR_SFTP_EXCEPTION, e);
        }
        log.info("Returning after uploading file to sftp.");
    }

    /**
     * This method can be used to download the file from file system to specific destination path.
     *
     * @param source      - The source file name to download from file system.
     * @param destination - The destination file name to save on local system.
     * @throws FileSystemException
     */
    @Override
    public void downloadFile(String source, String destination) throws FileSystemException {
        log.info("Received request for downloading a file from sftp.");
        try {
            sftpChannel.get(source, destination);
        } catch (SftpException e) {
            ErrorUtil.fileSystemException(ExceptionConstants.STR_SFTP_EXCEPTION, e);
        }
        log.info("Returning after downloading a file from sftp.");
    }

    /**
     * This method can be used to delete the file from file system.
     *
     * @param fileKey - The file name to delete.
     * @throws FileSystemException
     */
    @Override
    public void deleteFile(String fileKey) throws FileSystemException {
        log.info("Received request to delete file from sftp.");
        try {
            sftpChannel.rm(fileKey);
        } catch (SftpException e) {
            ErrorUtil.fileSystemException(ExceptionConstants.STR_SFTP_EXCEPTION, e);
        }
        log.info("Returning after deleting a file from sftp.");
    }

    /**
     * This method can be used to create the folder on file system.
     *
     * @param folderName - The folder name to create.
     * @throws FileSystemException
     */
    @Override
    public void createFolder(String folderName) throws FileSystemException {
        try {
            sftpChannel.mkdir(folderName);
            log.info("Folder created successfully");
        } catch (SftpException e) {
            ErrorUtil.fileSystemException(ExceptionConstants.STR_SFTP_EXCEPTION, e);
        }
    }

    /**
     * This method can be used to copy a file from source to destination on file system.
     *
     * @param sourceFilePath - The source file path to copy from.
     * @param targetFilePath - The target file path to save on file system.
     * @throws FileSystemException
     */
    @Override
    public void copyFile(String sourceFilePath, String targetFilePath) throws FileSystemException {
        log.info("Received request for copying a file from s3.");
        try {
            sftpChannel.put(sourceFilePath, targetFilePath);
        } catch (SftpException e) {
            ErrorUtil.fileSystemException(ExceptionConstants.STR_SFTP_EXCEPTION, e);
        }
        log.info("Returning after copying a file from s3.");
    }

    /**
     * This method can be used to get the configurations of file system.
     *
     * @return Returns the configurations {@link Configuration}
     */
    @Override
    public Configuration getConfiguration() {
        return this.config;
    }

    /**
     * This method will return the list of files available in the given input directory.
     *
     * @param directory - The directory name from where to read the files.
     * @return Returns the list of files available in the given input directory.
     * @throws FileSystemException
     */
    @Override
    public List<String> getListOfFiles(String directory) throws FileSystemException {
        log.info("Received request to get the list of files in directory: " + directory);
        List<String> files = new ArrayList<>();
        try {
            Vector ls = sftpChannel.ls(directory);
            for (int i = 0; i < ls.size(); i++) {
                LsEntry entry = (LsEntry) ls.get(i);
                files.add(entry.getFilename());
            }
        } catch (SftpException e) {
            ErrorUtil.fileSystemException(ExceptionConstants.STR_SFTP_EXCEPTION, e);
        }
        log.info("Returning response with the list of files from directory: " + directory);
        return files;
    }

}