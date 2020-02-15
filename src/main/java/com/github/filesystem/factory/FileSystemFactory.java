package com.github.filesystem.factory;

import com.github.filesystem.FileSystem;
import com.github.filesystem.annotation.Autowired;
import com.github.filesystem.annotation.Component;
import com.github.filesystem.annotation.ComponentScan;
import com.github.filesystem.exception.FileSystemException;
import com.github.filesystem.model.Configuration;
import com.github.filesystem.util.AppConstants;
import com.github.filesystem.util.ErrorUtil;
import com.github.filesystem.util.ExceptionConstants;
import com.github.filesystem.util.ValidationUtil;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The class will be used to get the file system for given configuration {@link Configuration}.
 *
 * @author Ram Alapure
 * @version 1.0
 * @since 12/02/2020
 */
public abstract class FileSystemFactory {

    protected FileSystemFactory() {
    }

    public static FileSystem getFileSystem(Configuration config) throws FileSystemException {
        ValidationUtil.rejectNull(config.getFileSystem(), "FileSystem");
        Optional<Class<?>> annotatedClass = getClass(config.getFileSystem());
        try {
            if (annotatedClass.isPresent()) {
                FileSystem fileSystem = (FileSystem) annotatedClass.get().newInstance();
                return fileSystem.configure(config);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            ErrorUtil.fileSystemException(ExceptionConstants.STR_INITIATION_EXCEPTION, e);
        }
        throw new FileSystemException(String.format("No such file system: %s found.", config.getFileSystem()));
    }

    private static Optional<Class<?>> getClass(String type) {
        // get sub type of this class to read package where components are available
        Reflections reflections = new Reflections(AppConstants.ENTRY_PACKAGE);
        Set<Class<? extends FileSystemFactory>> configClasses = reflections.getSubTypesOf(FileSystemFactory.class);

        // get the classes with annotation component scan
        Set<Class<? extends FileSystemFactory>> classSet = configClasses.stream()
                .filter(cls -> cls.isAnnotationPresent(ComponentScan.class)).collect(Collectors.toSet());

        // get the packages from classes with help of component scan
        List<String> packages = classSet.stream().map(cls -> cls.getAnnotation(ComponentScan.class).value())
                .collect(Collectors.toList());

        // get the type of implementation classes available in config
        Set<Class<?>> typeClasses = new HashSet();
        classSet.forEach(cls -> {
            for (Field field : cls.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class))
                    typeClasses.add(field.getType());
            }
        });

        // Get the classes only for the specified packages
        Reflections reflectionFilter = new Reflections(packages);
        Set<Class<?>> classes = new HashSet();
        typeClasses.forEach(typeClass -> classes.addAll(reflectionFilter.getSubTypesOf(typeClass)));

        return classes.stream().filter(cls -> type.equals(cls.getAnnotation(Component.class).value())).findFirst();
    }

}
