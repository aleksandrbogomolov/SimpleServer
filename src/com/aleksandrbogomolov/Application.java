package com.aleksandrbogomolov;

import com.aleksandrbogomolov.server.Server;
import com.aleksandrbogomolov.util.Properties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Application {

    public static void main(String[] args) {
        Server server = new Server(Properties.path);
        new Thread(server).start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String str;
            while (true) {
                str = reader.readLine();
                if ("exit".equals(str)) {
                    System.out.println("Stop server...");
                    server.stopServer();
                    server.closeAll();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
