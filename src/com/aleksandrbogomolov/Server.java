package com.aleksandrbogomolov;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static com.aleksandrbogomolov.Properties.GREETING;
import static com.aleksandrbogomolov.Properties.LS;

public class Server {

    private ServerSocket server;

    private File fileDirectory;

    private Boolean isServerStopped = false;

    private List<Connection> connections = Collections.synchronizedList(new ArrayList<>());

    private Map<String, Integer> fileDownloadCount = new ConcurrentHashMap<>();

    public Server() {
        try {
            this.server = new ServerSocket(Properties.PORT);
            this.fileDirectory = new File("~/Documents/Sport");
            getListFile().forEach(i -> fileDownloadCount.put(i, 0));
            while (!isServerStopped) {
                Socket socket = server.accept();
                Connection con = new Connection(socket);
                connections.add(con);
                con.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeAll();
        }
    }


    private Stream<String> getListFile() {return Arrays.stream(fileDirectory.list());}

    private void stopServer() {
        isServerStopped = true;
    }

    private void closeAll() {
        try {
            synchronized (connections) {
                connections.forEach(Connection::close);
            }
            if (server != null) server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Connection extends Thread {

        private BufferedReader in;

        private PrintWriter out;

        private Socket socket;

        private Boolean isConnectionStopped = false;

        Connection(Socket socket) {
            this.socket = socket;
            try {
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.out = new PrintWriter(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
                close();
            }
        }

        @Override
        public void run() {
            out.println(GREETING);
            String str;
            while (!isConnectionStopped) {
                try {
                    str = in.readLine();
                    switch (str) {
                        case "1": printListFile(); break;
                        case "2": loadFile(); break;
                        case "3": stopConnection(); break;
                        default: out.println("Введено неправильное значение!" + LS + GREETING);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    close();
                }
            }
        }

        void printListFile() {getListFile().forEach(out::println);}

        void loadFile() throws IOException {
            String sourceFile = in.readLine();
            Path source = FileSystems.getDefault().getPath(fileDirectory.getAbsolutePath() + "/" + sourceFile);
            Files.copy(source, Files.newOutputStream(source));
            fileDownloadCount.put(sourceFile, fileDownloadCount.get(sourceFile) != null ? fileDownloadCount.get(sourceFile) + 1 : 0);
        }

        void stopConnection() {isConnectionStopped = true;}

        @SuppressWarnings("Duplicates")
        void close() {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
