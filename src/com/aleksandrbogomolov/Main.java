package com.aleksandrbogomolov;

import com.aleksandrbogomolov.server.Server;
import com.aleksandrbogomolov.server.StopServer;

public class Main {

    public static void main(String[] args) {
        Server server = new Server();
        new Thread(server).start();
        try {
            Thread monitor = new StopServer();
            monitor.start();
            monitor.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        server.stopServer();
    }
}
