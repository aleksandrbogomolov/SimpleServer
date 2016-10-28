package com.aleksandrbogomolov.server;

import com.aleksandrbogomolov.util.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.aleksandrbogomolov.util.Properties.PORT;

public class Server implements Runnable {

    private ServerSocket server;

    private File fileDirectory;

    private boolean isServerStopped = false;

    private Logger logger;

    private StatisticCollector collector;

    private List<Connection> connections = Collections.synchronizedList(new ArrayList<>());

    private Map<String, Integer> fileDownloadCount = new ConcurrentHashMap<>();

    private Map<String, Integer> getFileDownloadCount() {
        return fileDownloadCount;
    }

    public Server(String path) {
        try {
            fileDirectory = new File(path);
            logger = new Logger();
            collector = new StatisticCollector();
            server = new ServerSocket(PORT);
            collector.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Made new server");
    }

    @Override
    public void run() {
        try {
            while (!isServerStopped) {
                Socket socket = server.accept();
                Connection con = new Connection(socket);
                connections.add(con);
                logger.info("Added new connection " + con.getName());
                con.start();
            }
        } catch (IOException e) {
            System.out.println("Server stopped by isServerStopped");
        } finally {
            closeAll();
        }
    }

    public void stopServer() {
        isServerStopped = true;
    }

    public void closeAll() {
        logger.info("Server stopped");
        try {
            synchronized (connections) {
                connections.forEach(Connection::interrupt);
            }
            collector.interrupt();
            if (server != null) server.close();
        } catch (IOException e) {
            System.out.println("Server streams mb not closed");
        }
    }

    private class Connection extends Thread {

        private BufferedReader in;

        private ObjectOutputStream out;

        private Socket socket;

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
                while (!isInterrupted()) {
                    str = in.readLine();
                    if ("1".equals(str)) printListFile();
                    else if ("2".equals(str)) loadFile();
                    else if ("3".equals(str)) this.interrupt();
                    sleep(10);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException i) {
                System.out.println("Connection " + this.getName() + " interrupted");
            } finally {
                close();
            }
        }

        void printListFile() throws IOException {
            String[] fileList = fileDirectory.list();
            if (fileList != null) {
                out.writeObject(new Message("message", fileList));
                out.flush();
            } else System.out.println("Directory not exist");
            logger.info("Print list files for " + this.getName());
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
            fileDownloadCount.put(sourceFile, fileDownloadCount.get(sourceFile) != null ? fileDownloadCount.get(sourceFile) + 1 : 1);
            logger.info("Connection " + this.getName() + " load file " + sourceFile);
        }

        @SuppressWarnings("Duplicates")
        void close() {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.out.println("Streams for " + this.getName() + " mb not closed");
            }
            logger.info("Connection " + this.getName() + " closed");
        }
    }

    private class StatisticCollector extends Thread {

        private final String pathToFile = System.getProperty("user.dir") + "/" + "statistic.txt";

        private Path path;

        StatisticCollector() {
            try {
                if (!new File(pathToFile).exists()) {
                    this.path = Files.createFile(Paths.get(pathToFile));
                } else {
                    this.path = Paths.get(pathToFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (!this.isInterrupted()) {
                StringBuilder reader = new StringBuilder();
                getFileDownloadCount().entrySet().stream().map(a -> a.getKey() + " : " + a.getValue() + System.lineSeparator()).forEach(reader::append);
                try {
                    Files.write(path, reader.toString().getBytes(), StandardOpenOption.CREATE);
                    sleep(10000);
                } catch (InterruptedException i) {
                    System.out.println("Statistic collector stopped");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
