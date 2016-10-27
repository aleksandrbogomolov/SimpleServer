package com.aleksandrbogomolov.server;

import java.io.Serializable;

public class Message implements Serializable {

    private String type;

    private String[] listFile;

    private byte[] file;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getListFile() {
        return listFile;
    }

    public void setListFile(String[] listFile) {
        this.listFile = listFile;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public Message(String type, String[] listFile) {
        this.type = type;
        this.listFile = listFile;
    }

    public Message(String type, byte[] body) {
        this.type = type;
        this.file = body;
    }
}
