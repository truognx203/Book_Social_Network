package com.xuntrng.book_social_network.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class FileUtils {
    public static byte[] readFileFromLocation(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            Path filePath = new File(url).toPath();
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.warn("No file found in path {}", url);
        }
        return null;
    }
}
