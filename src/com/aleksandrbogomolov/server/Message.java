package com.aleksandrbogomolov.server;

import java.io.Serializable;

public class Message implements Serializable {

    private String[] listFile;

    private byte[] file;

    public String[] getListFile() {
        return listFile;
    }

    public byte[] getFile() {
        return file;
    }

    Message(String[] listFile) {
        this.listFile = listFile;
    }

    Message(byte[] body) {
        this.file = body;
    }
}
