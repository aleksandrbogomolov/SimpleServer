package com.aleksandrbogomolov;

public abstract class Properties {

    public static final int PORT = 8282;

    public static final int STOP_PORT = 8283;

    public static final String IP = "localhost";

    public static final String LS = System.getProperty("line.separator");

    public static final String GREETING = "Для получения списка доступных файлов введите цифру 1" + LS + "Для того чтобы скачать файл введите 2" + LS + "Для выхода введите 3";
}
