package com.github.filesystem.client;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.github.filesystem.FileSystem;
import com.github.filesystem.annotation.Component;
import com.github.filesystem.exception.FileSystemException;
import com.github.filesystem.model.Configuration;
import com.github.filesystem.util.AppConstants;
import com.github.filesystem.util.ErrorUtil;
import com.github.filesystem.util.ExceptionConstants;
import com.github.filesystem.util.ValidationUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The AWS S3 client to perform operations on bucket.
 *
 * @author Ram Alapure
 * @version 1.0
 * @since 12/02/2020
 */
@Getter
@NoArgsConstructor
@Component(AppConstants.STR_S3)
public class AwsS3Client extends FileSystem {

    public static final Logger log = Logger.getLogger(AwsS3Client.class.getName());

    /**
     * The default timeout for creating new connections.
     */
    public static final int DEFAULT_CONNECTION_TIMEOUT = 30 * 1000;

    /**
     * The default timeout for reading from a connected socket.
     */
    public static final int DEFAULT_SOCKET_TIMEOUT = 60 * 1000;

    private AmazonS3 s3client;
    private Configuration config;

    /**
     * The method used for the file system configuration. It configure aws S3 client based on the s3 credentials.
     *
     * @param config
     * @return
     */
    public FileSystem configure(Configuration config) {
        log.info("Configuring the AWS S3 client.");
        this.config = config;
        Map<String, Object> properties = config.getProperties();
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setConnectionTimeout(DEFAULT_CONNECTION_TIMEOUT);
        clientConfiguration.setSocketTimeout(DEFAULT_SOCKET_TIMEOUT);
        if (properties.get(AppConstants.S3_ACCESS_KEY) != null) {
            AWSCredentials credentials = new BasicAWSCredentials(
                    properties.get(AppConstants.S3_ACCESS_KEY).toString(),
                    properties.get(AppConstants.S3_SECRET_KEY).toString());
            s3client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withRegion(Regions.valueOf(properties.get(AppConstants.REGION).toString()))
                    .withClientConfiguration(clientConfiguration).build();
        } else {
            s3client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.valueOf(properties.get(AppConstants.REGION).toString()))
                    .withClientConfiguration(clientConfiguration).build();
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
        Map<String, Object> properties = config.getProperties();
        S3Object fullObject = null;
        try {
            fullObject = s3client.getObject(new GetObjectRequest((String) properties.get(AppConstants.S3_BUCKET_NAME), filePath));
        } catch (SdkClientException e) {
            ErrorUtil.fileSystemException(ExceptionConstants.STR_AWS_EXCEPTION, e);
        }
        return fullObject.getObjectContent();
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
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(inputStream.available());
            String bucketName = (String) config.getProperties().get(AppConstants.S3_BUCKET_NAME);
            s3client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));
            log.info("File upload operation is successful");
        } catch (IOException e) {
            ErrorUtil.fileSystemException(ExceptionConstants.STR_IO_EXCEPTION, e);
        }
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
        log.info("Received request for downloading a file from s3.");
        try (FileOutputStream fos = new FileOutputStream(new File(destination))) {
            S3Object o = s3client.getObject(new GetObjectRequest(
                    (String) config.getProperties().get(AppConstants.S3_BUCKET_NAME), source));
            S3ObjectInputStream s3is = o.getObjectContent();

            byte[] readBuf = new byte[1024];
            int readLen = 0;
            while ((readLen = s3is.read(readBuf)) > 0) {
                fos.write(readBuf, 0, readLen);
            }
            s3is.close();
        } catch (AmazonServiceException e) {
            ErrorUtil.fileSystemException(ExceptionConstants.STR_AWS_EXCEPTION, e);
        } catch (FileNotFoundException e) {
            ErrorUtil.fileSystemException(String.format(ExceptionConstants.STR_FILE_EXCEPTION, destination), e);
        } catch (IOException e) {
            ErrorUtil.fileSystemException(ExceptionConstants.STR_IO_EXCEPTION, e);
        }
        log.info("Returning after downloading a file from s3.");
    }

    /**
     * This method can be used to delete the file from file system.
     *
     * @param fileKey - The file name to delete.
     * @throws FileSystemException
     */
    @Override
    public void deleteFile(String fileKey) throws FileSystemException {
        ValidationUtil.rejectNull(fileKey, "FileKey");
        log.info("Received request to delete file from s3.");
        s3client.deleteObject((String) config.getProperties().get(AppConstants.S3_BUCKET_NAME), fileKey);
        log.info("Returning after deleting a file from s3.");
    }

    /**
     * This method can be used to create the folder on file system.
     *
     * @param folderName - The folder name to create.
     * @throws FileSystemException
     */
    @Override
    public void createFolder(String folderName) throws FileSystemException {
        // create meta-data for your folder and set content-length to 0
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);
        // create empty content
        InputStream emptyStream = new ByteArrayInputStream(new byte[0]);
        // create a PutObjectRequest passing the folder name suffixed by /
        PutObjectRequest putObjectRequest = new PutObjectRequest(
                (String) config.getProperties().get(AppConstants.S3_BUCKET_NAME),
                folderName + AppConstants.CHAR_FS, emptyStream, metadata);
        // send request to S3 to create folder
        s3client.putObject(putObjectRequest);
        log.info("Folder created successfully");
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
            String bucketName = (String) config.getProperties().get(AppConstants.S3_BUCKET_NAME);
            // Copy the object into a new object in the same bucket.
            CopyObjectRequest copyObjRequest = new CopyObjectRequest(bucketName, sourceFilePath, bucketName, targetFilePath);
            s3client.copyObject(copyObjRequest);
        } catch (SdkClientException e) {
            ErrorUtil.fileSystemException(ExceptionConstants.STR_AWS_EXCEPTION, e);
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
        log.info("Received request to read list of objects from S3 from given directory.");
        try {
            ListObjectsV2Request req = new ListObjectsV2Request()
                    .withBucketName((String) config.getProperties().get(AppConstants.S3_BUCKET_NAME))
                    .withPrefix(directory);
            ListObjectsV2Result listOfObjects = s3client.listObjectsV2(req);
            List<String> filesPath = listOfObjects.getObjectSummaries().stream().map(obj -> obj.getKey()).collect(Collectors.toList());
            log.info("Returning response after reading list of objects from S3 from given directory.");
            return filesPath;
        } catch (Exception e) {
            ErrorUtil.fileSystemException(ExceptionConstants.STR_AWS_EXCEPTION, e);
        }
        return new ArrayList<>();
    }

    /**
     * This method can be used to get the list of bucket.
     *
     * @return
     * @throws FileSystemException
     */
    public List<String> getListOfBuckets() throws FileSystemException {
        log.info("Received request to get the list of buckets from S3.");
        return s3client.listBuckets().stream().map(Bucket::getName).collect(Collectors.toList());
    }

    /**
     * This method can be used to create the bucket on S3.
     *
     * @param bucketName - The bucket name to create.
     * @throws FileSystemException
     */
    public void createBucket(String bucketName) throws FileSystemException {
        log.info("Received request to create the bucket on S3.");
        if (!s3client.doesBucketExistV2(bucketName)) {
            try {
                s3client.createBucket(bucketName);
                log.info("Returning a response after creating the bucket on S3.");
            } catch (Exception e) {
                ErrorUtil.fileSystemException(ExceptionConstants.STR_AWS_EXCEPTION, e);
            }
        } else {
            ErrorUtil.fileSystemException(String.format("The bucket: %s already exist on S3.", bucketName));
        }
    }

    /**
     * This method can be used to delete the bucket. Underline it will delete the objects and versions from bucket.
     *
     * @param bucketName - The bucket name to delete.
     * @throws FileSystemException
     */
    public void deleteBucket(String bucketName) throws FileSystemException {
        log.info("Received request to delete the bucket from S3.");
        if (s3client.doesBucketExistV2(bucketName)) {
            try {
                log.info("Removing objects from bucket");
                ObjectListing object_listing = s3client.listObjects(bucketName);
                while (true) {
                    for (Iterator<?> iterator =
                         object_listing.getObjectSummaries().iterator();
                         iterator.hasNext(); ) {
                        S3ObjectSummary summary = (S3ObjectSummary) iterator.next();
                        s3client.deleteObject(bucketName, summary.getKey());
                    }
                    // more object_listing to retrieve?
                    if (object_listing.isTruncated()) {
                        object_listing = s3client.listNextBatchOfObjects(object_listing);
                    } else {
                        break;
                    }
                }

                log.info("Removing versions from bucket");
                VersionListing version_listing = s3client.listVersions(
                        new ListVersionsRequest().withBucketName(bucketName));
                while (true) {
                    for (Iterator<?> iterator =
                         version_listing.getVersionSummaries().iterator();
                         iterator.hasNext(); ) {
                        S3VersionSummary vs = (S3VersionSummary) iterator.next();
                        s3client.deleteVersion(bucketName, vs.getKey(), vs.getVersionId());
                    }

                    if (version_listing.isTruncated()) {
                        version_listing = s3client.listNextBatchOfVersions(version_listing);
                    } else {
                        break;
                    }
                }
                s3client.deleteBucket(bucketName);
                log.info("Returning a response after deleting the bucket from S3.");
            } catch (Exception e) {
                ErrorUtil.fileSystemException(ExceptionConstants.STR_AWS_EXCEPTION, e);
            }
        } else {
            ErrorUtil.fileSystemException(String.format("The bucket: %s does not exist on S3.", bucketName));
        }
    }

}