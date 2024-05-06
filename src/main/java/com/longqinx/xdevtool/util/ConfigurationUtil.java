package com.longqinx.xdevtool.util;

import com.intellij.openapi.project.Project;
import com.longqinx.xdevtool.bean.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author xiaos
 * @since 2023/10/24
 */
public class ConfigurationUtil {
    private static final String DIR_NAME = "/.dev-tool/";
    private static final String CONF_FILE_NAME = "conf.json";

    public static Configuration loadFromDisk(Project project) {
        Path confFile = Paths.get(project.getBasePath() + DIR_NAME + CONF_FILE_NAME);
        if (!Files.exists(confFile)) {
            return new Configuration();
        }
        Configuration configuration = null;
        try {
            configuration = JsonUtil.toObject(Files.readString(confFile), Configuration.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return configuration == null ? new Configuration() : configuration;
    }

    public static void update(Project project, Configuration configuration) {
        Path confDir = Paths.get(project.getBasePath() + DIR_NAME);
        try {
            if (!Files.exists(confDir)) {
                Files.createDirectories(confDir);
            }
            Path confFile = confDir.resolve(CONF_FILE_NAME);
            if (!Files.exists(confFile)) {
                Files.createFile(confFile);
            }
            String content = JsonUtil.toJson(configuration);
            Files.writeString(confFile, content == null ? "" : content, StandardOpenOption.SYNC);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
