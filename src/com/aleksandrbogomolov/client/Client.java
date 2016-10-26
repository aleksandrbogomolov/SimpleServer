package com.aleksandrbogomolov.client;

import com.aleksandrbogomolov.Properties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Client extends Thread {

    private BufferedReader in;

    private PrintWriter out;

    private Socket socket;

    private Scanner scanner;

    public static void main(String[] args) {
        new Client().start();
    }

    private Client() {
        try {
            scanner = new Scanner(System.in);
            socket = new Socket(Properties.IP, Properties.PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String str;
            RedOut redOut = new RedOut();
            redOut.start();
            while (true) {
                str = scanner.nextLine();
                out.println(str);
                if ("2".equals(str)) saveFile();
                else if ("3".equals(str)) {
                    redOut.setStop();
                    close();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private void saveFile() throws IOException {
        System.out.println("Введите полное название скачиваемого файла");
        String source = scanner.nextLine();
        out.println(source);
        System.out.println("Введите адресс директории для сохранения файла");
        Path target = Paths.get(scanner.nextLine() + "/" + source);
        Files.copy(socket.getInputStream(), target);
        System.out.println("Файл сохранен");
    }

    @SuppressWarnings("Duplicates")
    private void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class RedOut extends Thread {

        private boolean isStopped;

        void setStop() {
            isStopped = true;
        }

        @Override
        public void run() {
            try {
                String str;
                while (!isStopped) {
                    str = in.readLine();
                    System.out.println(str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
