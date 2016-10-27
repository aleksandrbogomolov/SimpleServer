package com.aleksandrbogomolov.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.aleksandrbogomolov.Properties.PORT;

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

        private ObjectOutputStream out;

        private Socket socket;

        private boolean isConnectionStopped = false;

        Connection(Socket socket) {
            try {
                this.socket = socket;
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.out = new ObjectOutputStream(socket.getOutputStream());
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

        void printListFile() throws IOException {
            String[] fileList = fileDirectory.list();
            if (fileList != null) {
                out.writeObject(new Message("message", fileList));
                out.flush();
            } else System.out.println("Директория по введенному пути отсутсвует");
        }

        void loadFile() throws IOException {
            String sourceFile = in.readLine();
            File source = new File(fileDirectory.getAbsolutePath() + "/" + sourceFile);
            byte[] bytes = new byte[(int) source.length()];
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(source));
            inputStream.read(bytes, 0, bytes.length);
            out.writeObject(new Message("file", bytes));
            out.flush();
            inputStream.close();
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
