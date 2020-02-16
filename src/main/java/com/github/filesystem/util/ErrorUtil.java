package com.github.filesystem.util;

import com.github.filesystem.exception.FileSystemException;

import java.util.logging.Logger;

/**
 * Error utility to print and throw the file system exception.
 *
 * @author Ram Alapure
 * @version 1.0
 * @since 12/02/2020
 */
public final class ErrorUtil {

    public static final Logger log = Logger.getLogger(ErrorUtil.class.getName());

    private ErrorUtil() {
    }

    /**
     *
     * @param message
     * @param e
     * @throws FileSystemException
     */
    public static void fileSystemException(String message, Exception e) throws FileSystemException {
        log.severe(String.format("%sCause: %s%s", message, AppConstants.STR_BRACES, e.getCause()));
        throw new FileSystemException(message);
    }

    /**
     *
     * @param message
     * @throws FileSystemException
     */
    public static void fileSystemException(String message) throws FileSystemException {
        log.severe(message);
        throw new FileSystemException(message);
    }

}
