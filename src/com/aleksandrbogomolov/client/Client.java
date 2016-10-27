package com.aleksandrbogomolov.client;

import com.aleksandrbogomolov.Properties;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread {

    private PrintWriter out;

    private Socket socket;

    private Scanner scanner;

    private final String LS = System.getProperty("line.separator");

    private final String GREETING = "Для получения списка доступных файлов введите цифру 1" + LS + "Для того чтобы скачать файл введите 2" + LS + "Для выхода введите 3";

    public static void main(String[] args) {
        new Client().start();
    }

    private Client() {
        try {
            scanner = new Scanner(System.in);
            socket = new Socket(Properties.IP, Properties.PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            System.out.println(GREETING);
            String str;
            while (true) {
                str = scanner.nextLine();
                out.println(str);
                if ("1".equals(str)) {
                    new ReadOut().start();
                } else if ("2".equals(str)) {
                    saveFile();
                } else if ("3".equals(str)) {
                    close();
                    break;
                } else System.out.println("Введено неправильное значение!" + LS + GREETING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private InputStream getIs() throws IOException {
        return socket.getInputStream();
    }

    private void saveFile() throws IOException {
        System.out.println("Введите полное название скачиваемого файла");
        String source = scanner.nextLine();
        System.out.println("Введите адресс директории для сохранения файла");
        File target = new File(scanner.nextLine() + "/" + source);
        out.println(source);
        Worker worker = new Worker(target);
        worker.start();
//        byte[] bytes = new byte[1];
//        int bytesRead;
//        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
//        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(target));
//
//        do {
//            bytesRead = is.read(bytes, 0, bytes.length);
//            arrayOutputStream.write(bytes);
//        } while (bytesRead != -1);
//        outputStream.write(arrayOutputStream.toByteArray());
//        outputStream.flush();
//        outputStream.close();
//
//        System.out.println("Файл сохранен");
    }

    @SuppressWarnings("Duplicates")
    private void close() {
        try {
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ReadOut extends Thread {

        private BufferedReader reader;

//        private boolean isStopped;

//        void setStop() {
//            isStopped = true;
//            try {
//                reader.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        @Override
        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(getIs()));
                String str;
                while ((str = reader.readLine()) != null) {
                    System.out.println(str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class Worker extends Thread {

        private File file;

        Worker(File file) {
            this.file = file;
        }

        @Override
        public void run() {
            try {
                if (socket.isClosed()) socket = new Socket(Properties.IP, Properties.PORT);
                byte[] bytes = new byte[1];
                int bytesRead;
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
                InputStream inputStream = getIs();

                do {
                    bytesRead = inputStream.read(bytes, 0, bytes.length);
                    arrayOutputStream.write(bytes);
                } while (bytesRead != -1);
                outputStream.write(arrayOutputStream.toByteArray());
                outputStream.flush();

                arrayOutputStream.close();
                inputStream.close();
                outputStream.close();

                System.out.println("Файл сохранен");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
