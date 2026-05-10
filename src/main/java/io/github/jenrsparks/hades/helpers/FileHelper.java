package io.github.jenrsparks.hades.helpers;

import java.io.File;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileHelper.class);

    public static File getFileWithFallbackResource(File file, String resourcePath) {
        if (file != null) {
            if(file.exists() && file.isFile()) {
                return file;
            } else {
                LOGGER.warn("Specified file '" + file.getAbsolutePath() + "' does not exist or is not a regular file.");
                // fallthrough to fallback resource method call
            }
        }
        return getFileFromResource(resourcePath);
    }
    
    public static File getFileFromResource(String resourcePath) {
        URL resource = FileHelper.class.getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Default resource not found: " + resourcePath);
        } else {
            return new File(resource.getFile());
        }
    }

}
