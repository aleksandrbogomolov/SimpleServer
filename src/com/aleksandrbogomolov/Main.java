package com.aleksandrbogomolov;

import com.aleksandrbogomolov.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.Thread.sleep;

public class Main {

    public static void main(String[] args) {
        Server server = new Server();
        Thread thread = new Thread(server);
        thread.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String str;
            while (true) {
                str = reader.readLine();
                if ("exit".equals(str)) {
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
