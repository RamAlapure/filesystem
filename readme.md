The library cab be used to perform file system operations, currently it support AWS S3 and SFTP related operations.

How you can use it:

**Maven**
```
<dependency>
  <groupId>com.github.ramalapure</groupId>
  <artifactId>filesystem</artifactId>
  <version>1.1</version>
</dependency>
```

**Gradle**
```
implementation 'com.github.ramalapure:filesystem:1.1'
```

Let's see the configuration and how to get the instance of file system.

**for AWS S3 file system:**
```
Map<String, Object> properties = new HashMap<>();
properties.put(AppConstants.S3_ACCESS_KEY, "REPLACE_AWS_ACCESS_KEY");
properties.put(AppConstants.S3_SECRET_KEY, "REPLACE_AWS_SECRET_KEY");
properties.put(AppConstants.REGION, "REPLACE_REGION");
properties.put(AppConstants.S3_BUCKET_NAME, "REPLACE_BUCKET_NAME");
Configuration config = new Configuration(AppConstants.STR_S3, properties);
FileSystem fileSystem = FileSystemFactory.getFileSystem(config);
```
**for SFTP file system:**
```
Map<String, Object> properties = new HashMap<>();
properties.put(AppConstants.SFTP_USERNAME, "REPLACE_SFTP_USERNAME");
properties.put(AppConstants.SFTP_PASSWORD, "REPLACE_SFTP_PSWD");
properties.put(AppConstants.SFTP_HOSTNAME, "REPLACE_SFTP_HOST"); 
// port value must be integer
properties.put(AppConstants.SFTP_PORT, REPLACE_SFTP_PORT);
Configuration config = new Configuration(AppConstants.STR_SFTP, properties);
FileSystem fileSystem = FileSystemFactory.getFileSystem(config);
```

The AWS S3 file system supports following operations on bucket:
 1. Create new bucket
    ```
    // As creating a bucket is not directly available in file system interface 
    // we need to get the actual instance of S3 client.
    AwsS3Client client = (AwsS3Client) fileSystem;
    client.createBucket("REPLACE_WITH_BUCKET_NAME");
    ```
 2. Get list of buckets
    ```
    // As list of buckets is not directly available in file system interface 
    // we need to get the actual instance of S3 client.
    AwsS3Client client = (AwsS3Client) fileSystem;
    List<String> listOfBuckets = client.getListOfBuckets();
    ```
 3. Get list of files from specific folder or bucket
    ```
    // Empty string will return all the objects/files from buckets
    List<String> files = fileSystem.getListOfFiles("");
    //OR you can get files from only specific folder
    List<String> files = fileSystem.getListOfFiles("/specific-folder");
    ```
 4. Upload file/object to bucket
    ```
    File file = new File("THE_FILE_YOU_WANT_TO_UPLOAD");
    fileSystem.uploadFile(new FileInputStream(file), "FILE_KEY");
    ```
 5. Read input stream of file from bucket
    ```
    InputStream inputStream = fileSystem.read("FILE_KEY");
    ```
 6. Download the file from bucket to local system
    ```
    fileSystem.downloadFile("FILE_KEY_TO_DOWNLOAD_FROM_BUCKET", "FILE_NAME_TO_SAVE_ON_LOCAL_SYSTEM");
    ```
 7. Create folder in bucket
    ```
    fileSystem.createFolder("FOLDER_NAME");
    ```
 8. Delete file from bucket
    ```
    fileSystem.deleteFile("FILE_KEY");
    ```
 9. Copy file/object from one folder to another in same bucket
    ```
    fileSystem.copyFile("SOURCE_FILE_KEY", "TARGET_FILE_KEY");
    ```
 10. Delete the bucket with objects and versions
     ```
     // As deleting a bucket is not directly available in file system interface 
     // we need to get the actual instance of S3 client.
     AwsS3Client client = (AwsS3Client) fileSystem;
     client.deleteBucket("BUCKET_NAME");
     ```
     
The SFTP file system supports following operations on directory/file:
 1. Get list of files
    ```
    // Empty string will return all the files
    List<String> files = fileSystem.getListOfFiles("");
    //OR you can get files from only specific folder
    List<String> files = fileSystem.getListOfFiles("/specific-folder");
    ```
 2. Upload file
    ```
    File file = new File("THE_FILE_YOU_WANT_TO_UPLOAD");
    fileSystem.uploadFile(new FileInputStream(file), "FILE_KEY");
    ```
 3. Read input stream of file
    ```
    InputStream inputStream = fileSystem.read("FILE_NAME_WITH_PATH");
    ```
 4. Download file to local system
    ```
    fileSystem.downloadFile("FILE_NAME_TO_DOWNLOAD_FROM_DIRECTORY", "FILE_NAME_TO_SAVE_ON_LOCAL_SYSTEM");
    ```
 5. Create new folder
    ```
    fileSystem.createFolder("FOLDER_NAME");
    ```
 6. Delete file/directory
    ```
    fileSystem.deleteFile("FILE_NAME_WITH_PATH");
    ```
 7. Copy file from one folder to another
    ```
    fileSystem.copyFile("SOURCE_FILE_NAME_WITH_PATH", "TARGET_FILE_NAME_WITH_PATH");
    ```