package com.help.service.zip;

public interface ZipService {
    void open();

    void write(String name, byte[] bytes);

    void close();

    byte[] getBytes();
}
