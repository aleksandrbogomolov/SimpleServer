package com.aleksandrbogomolov;

import com.aleksandrbogomolov.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Application {

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter path to directory");
        Server server = null;
        try {
            server = new Server(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(server).start();
        try {
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
