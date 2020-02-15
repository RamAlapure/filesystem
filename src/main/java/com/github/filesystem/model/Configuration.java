package com.github.filesystem.model;

import java.util.Map;
import java.util.StringJoiner;

/**
 * The configuration class to create file system client.
 * e.g. fileSystem = S3/SFTP,
 * properties:
 * for S3 - S3_BUCKET_NAME, S3_ACCESS_KEY, S3_SECRET_KEY, REGION
 * for SFTP - SFTP_HOSTNAME, SFTP_PORT(int), SFTP_USERNAME, SFTP_PASSWORD
 *
 * @author Ram Alapure
 * @version 1.0
 * @since 12/02/2020
 */

public class Configuration {

    private String fileSystem;
    private Map<String, Object> properties;

    public Configuration(String fileSystem, Map<String, Object> properties) {
        this.fileSystem = fileSystem;
        this.properties = properties;
    }

    public String getFileSystem() {
        return fileSystem;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Configuration.class.getSimpleName() + "[", "]")
                .add("fileSystem='" + fileSystem + "'")
                .add("properties=" + properties)
                .toString();
    }
}
