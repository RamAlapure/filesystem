package com.github.filesystem.config;

import com.github.filesystem.FileSystem;
import com.github.filesystem.annotation.Autowired;
import com.github.filesystem.annotation.ComponentScan;
import com.github.filesystem.factory.FileSystemFactory;

/**
 * This class will provide the file system implementation package info.
 *
 * @author Ram Alapure
 * @version 1.0
 * @since 12/02/2020
 */
@ComponentScan("com.github.filesystem.client")
public class FileSystemConfiguration extends FileSystemFactory {

    @Autowired
    private FileSystem fileSystem;

}
