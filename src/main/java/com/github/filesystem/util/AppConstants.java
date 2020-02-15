package com.github.filesystem.util;

/**
 * The common application constants.
 *
 * @author Ram Alapure
 * @version 1.0
 * @since 12/02/2020
 */
public final class AppConstants {

    private AppConstants() {
    }

    // Entry package to scan file system clients
    public static final String ENTRY_PACKAGE = "com.github.filesystem";

    // file system clients
    public static final String STR_S3 = "S3";
    public static final String STR_SFTP = "SFTP";

    // S3 properties
    public static final String S3_BUCKET_NAME = "S3_BUCKET_NAME";
    public static final String S3_ACCESS_KEY = "S3_ACCESS_KEY";
    public static final String S3_SECRET_KEY = "S3_SECRET_KEY";
    public static final String REGION = "REGION";

    // SFTP properties
    public static final String SFTP_USERNAME = "SFTP_USERNAME";
    public static final String SFTP_PASSWORD = "SFTP_PASSWORD";
    public static final String SFTP_HOSTNAME = "SFTP_HOSTNAME";
    public static final String SFTP_PORT = "SFTP_PORT";


    public static final CharSequence CHAR_FS = "/";
    public static final String CHAR_COMMA = ",";
    public static final String CSV_DELIMITER = ",";

    public static final String STR_BRACES = "{}";
    public static final String STR_DOUBLE_BS = "\\";

    public static final String REGEX = "(\\[[0-9]*\\]$)";
    public static final String CSV_SPLIT_PATTERN = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
}
