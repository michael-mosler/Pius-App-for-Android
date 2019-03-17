package com.rmkrings.helper;

import android.content.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Logger;

import com.rmkrings.main.PiusApp;

public class Cache {
    private final static Logger logger = Logger.getLogger(Cache.class.getName());

    public boolean fileExists(String filename) {
        File file = PiusApp.getAppContext().getFileStreamPath(filename);
        return file.exists();
    }

    // Store data under given filename in cache directory.
    public void store(String filename, byte[] data) {
        try {
            FileOutputStream fos = PiusApp.getAppContext().openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(data);
        }
        catch (java.io.FileNotFoundException e) {
            logger.severe(String.format("Failed to open file %s: File not found.", filename));
        }
        catch (java.io.IOException e) {
            logger.severe(String.format("Failed to write to file %s: %s", filename, e.toString()));
        }
    }

    // Read data from filename in cache directory. If data cannot
    // be read returns nil. Supposes that file content is a string.
    public byte[] read(String filename) {
        byte[] data = {};

        try {
            FileInputStream fis = PiusApp.getAppContext().openFileInput(filename);
            fis.read(data);
        }
        catch (java.io.FileNotFoundException e) {
            logger.severe(String.format("Failed to open file %s: File not found.", filename));
        }
        catch (java.io.IOException e) {
            logger.severe(String.format("Failed to read from file %s: %s", filename, e.toString()));
        }

        return data;
    }
}
