package util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import orm.Table;

import aex.Main;

public class Util {

    public static String getResourceFileAsString(Class<?> clazz, String file) {

        InputStream in = clazz.getResourceAsStream(file);
        if (in == null) {
            throw new RuntimeException("Resouce not found!");
        }

        try {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("The unspeakable!");
        }
    }

    public static Table[] parseCollectionSample(String collectionName) {
        // getResourceFileAsString(, file);
        return null;
    }
}
