package com.aleksandrbogomolov.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.aleksandrbogomolov.Properties.*;

public class Server implements Runnable {

    private ServerSocket server;

    private File fileDirectory;

    private boolean isServerStopped = false;

    private List<Connection> connections = Collections.synchronizedList(new ArrayList<>());

    private Map<String, Integer> fileDownloadCount = new ConcurrentHashMap<>();

    public Server() {
        try {
            fileDirectory = new File("/Volumes/Macintosh HD/Documents/Sport");
            server = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
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

    public void stopServer() {
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

        private boolean isConnectionStopped = false;

        Connection(Socket socket) {
            try {
                this.socket = socket;
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String str;
                while (!isConnectionStopped) {
                    str = in.readLine();
                    if ("1".equals(str)) printListFile();
                    else if ("2".equals(str)) loadFile();
                    else if ("3".equals(str)) stopConnection();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close();
            }
        }

        void printListFile() {
            String[] fileList = fileDirectory.list();
            if (fileList != null) {
                Arrays.stream(fileList).forEach(out::println);
            } else System.out.println("Директория по введенному пути отсутсвует");
        }

        void loadFile() throws IOException {
            String sourceFile = in.readLine();
            File source = new File(fileDirectory.getAbsolutePath() + "/" + sourceFile);
            byte[] bytes = new byte[(int) source.length()];
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(source));
            BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
            inputStream.read(bytes, 0 , bytes.length);
            outputStream.write(bytes, 0, bytes.length);
            outputStream.flush();
            inputStream.close();
            outputStream.close();
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
