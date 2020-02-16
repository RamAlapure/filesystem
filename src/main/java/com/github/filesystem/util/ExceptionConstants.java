package com.github.filesystem.util;

/**
 * This class to provide information on exceptions.
 *
 * @author Ram Alapure
 * @version 1.0
 * @since 12/02/2020
 */
public class ExceptionConstants {

    private ExceptionConstants() {
    }

    public static final String STR_IO_EXCEPTION = "The IO exception occurred while reading a file.";
    public static final String STR_SFTP_EXCEPTION = "The SFTP connection exception occurred.";
    public static final String STR_AWS_EXCEPTION = "The AWS S3 connection exception occurred.";
    public static final String STR_FILE_EXCEPTION = "The specified file: %s is not found for processing.";
    public static final String STR_INITIATION_EXCEPTION = "Problem instantiating or accessing a new instance.";
    public static final String STR_PARSING_EXCEPTION = "The exception occurred while parsing the file.";


}
