package com.aleksandrbogomolov.client;

import com.aleksandrbogomolov.server.Message;
import com.aleksandrbogomolov.util.Properties;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class Client extends Thread {

    private final String LS = System.getProperty("line.separator");

    private final String GREETING = LS + "Для получения списка доступных файлов введите цифру 1" + LS + "Для того чтобы скачать файл введите 2" + LS + "Для выхода введите 3";

    private Socket socket;

    private ObjectInputStream in;

    private PrintWriter out;

    private final Scanner scanner = new Scanner(System.in);

    public Client() {
        try {
            this.socket = new Socket(Properties.IP, Properties.PORT);
            this.in = new ObjectInputStream(socket.getInputStream());
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Client().start();
    }

    @Override
    public void run() {
        try {
            printGreeting();
            String str;
            while (socket != null) {
                str = scanner.nextLine();
                out.println(str);
                if ("1".equals(str)) {
                    Arrays.stream(readMessage().getListFile()).forEach(System.out::println);
                    printGreeting();
                } else if ("2".equals(str)) {
                    saveFile();
                    printGreeting();
                } else if ("3".equals(str)) {
                    close();
                    break;
                } else System.out.println("Введено неправильное значение!" + LS + LS + GREETING);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Server not answer");
        } finally {
            close();
        }
    }

    private void printGreeting() { System.out.println(GREETING); }

    private Message readMessage() throws ClassNotFoundException, IOException {
        return (Message) in.readObject();
    }

    private void saveFile() throws ClassNotFoundException, IOException {
        System.out.println("Введите полное название скачиваемого файла, например \"test.txt\"");
        String source = scanner.nextLine();
        System.out.println("Введите адрес директории для сохранения файла, например \"C://Downloads\"");
        out.println(source);

        File target = new File(scanner.nextLine() + "/" + source);
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(target));
        outputStream.write(readMessage().getFile());
        outputStream.flush();
        outputStream.close();

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
}
