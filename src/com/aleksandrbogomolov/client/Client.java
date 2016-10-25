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

public class Client {

    private BufferedReader in;

    private PrintWriter out;

    private Socket socket;

    private Scanner scanner;

    public Client() {
        try {
            this.scanner = new Scanner(System.in);
            this.socket = new Socket(Properties.IP, Properties.PORT);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream());

            String str = "";
            while (!"3".equals(str)) {
                str = scanner.nextLine();
                out.println(str);
                switch (str) {
                    case "1": readListFile(); break;
                    case "2": saveFile(); break;
                    case "3": close(); break;
                    default: readOut();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private void readListFile() throws IOException {
        readOut();
    }

    private void saveFile() throws IOException {
        System.out.println("Введите полное название скачиваемого файла");
        out.println(scanner.nextLine());
        System.out.println("Введите адресс директории для сохранения файла");
        Path target = Files.createFile(Paths.get(scanner.nextLine()));
        Files.copy(socket.getInputStream(), target);
    }

    private void readOut() throws IOException {
        String str;
        while ((str = in.readLine()) != null) {
            System.out.println(str);
        }
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
}
