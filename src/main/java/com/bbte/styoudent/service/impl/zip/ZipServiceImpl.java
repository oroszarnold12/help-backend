package com.bbte.styoudent.service.impl.zip;

import com.bbte.styoudent.service.ServiceException;
import com.bbte.styoudent.service.zip.ZipService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ZipServiceImpl implements ZipService {
    private ByteArrayOutputStream zip;
    private ZipOutputStream out;

    @Override
    public void open() {
        zip = new ByteArrayOutputStream();
        out = new ZipOutputStream(zip);
    }

    @Override
    public void write(String name, byte[] bytes) {
        ZipEntry entry = new ZipEntry(name);
        try {
            out.putNextEntry(entry);
            out.write(bytes);
            out.closeEntry();
        } catch (IOException exception) {
            throw new ServiceException("Failed to write to zip!", exception);
        }
    }

    @Override
    public void close() {
        try {
            out.close();
        } catch (IOException exception) {
            throw new ServiceException("Failed to close zip!", exception);
        }
    }

    @Override
    public byte[] getBytes() {
        return zip.toByteArray();
    }
}
