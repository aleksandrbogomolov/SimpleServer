package com.aleksandrbogomolov.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public class Logger {

    private final String pathToFile = System.getProperty("user.dir") + "/" + "server_log.log";

    private Path log;

    public Logger() {
        try {
            if (!new File(pathToFile).exists()) {
                this.log = Files.createFile(Paths.get(pathToFile));
            } else {
                this.log = Paths.get(pathToFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void info(String s) {
        String result = LocalDateTime.now() + " : " + s + System.lineSeparator();
        try {
            Files.write(log, result.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
