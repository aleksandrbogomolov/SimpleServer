package com.aleksandrbogomolov;

abstract class Properties {

    static final int PORT = 8282;

    static final int STOP_PORT = 8283;

    static final String IP = "127.0.0.1";

    static final String LS = System.getProperty("line.separator");

    static final String GREETING = "Для получения списка доступных файлов введите цифру 1" + LS + "Для того чтобы скачать файл введите 2" + LS + "Для выхода введите 3";
}
