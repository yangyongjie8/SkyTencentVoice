package com.skyworthdigital.voice.dingdang.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


public class NetUtils {
    public static boolean writeContentToFile(InputStream stream, File file) {
        if (file == null || stream == null) {
            return false;
        }
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            copyStream(stream, outputStream);
            outputStream.close();
            stream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                stream.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return false;
    }

    private static void copyStream(InputStream inputStream, OutputStream outputStream) {
        final int bufferSize = 1024;
        try {
            byte[] bytes = new byte[bufferSize];
            for (; ; ) {
                int count = inputStream.read(bytes, 0, bufferSize);
                if (count == -1) {
                    break;

                }
                outputStream.write(bytes, 0, count);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
