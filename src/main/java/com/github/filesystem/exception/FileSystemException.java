package com.github.filesystem.exception;

/**
 * Signals that a file system exception of some sort has occurred.
 *
 * @author ralapure
 */
public class FileSystemException extends Exception {

    private static final long serialVersionUID = 3009370493293321348L;

    /**
     * Constructs an {@code FileSystemException} with the specified detail
     * message.
     *
     * @param message The detail message (which is saved for later retrieval by the
     *                {@link #getMessage()} method)
     */
    public FileSystemException(String message) {
        super(message);
    }

}
