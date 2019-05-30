package com.skyworthdigital.voice.common.utils;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by SDT03046 on 2018/11/16.
 */

public class WavHeadHelper {
    public static void writeHEAD(FileOutputStream output, int channel, int sampleRate, int nBitsPerSample, int encodeMode) {
        if (output == null) return;
        try {
            String uRiffFcc = "RIFF";
            byte[] b_uRiffFcc = uRiffFcc.getBytes("US-ASCII");
            String uWaveFcc = "WAVE";
            byte[] b_uWaveFcc = uWaveFcc.getBytes("US-ASCII");
            String uFmtFcc = "fmt ";
            byte[] b_uFmtFcc = uFmtFcc.getBytes("US-ASCII");
            String uDataFcc = "data";
            byte[] b_uDataFcc = uDataFcc.getBytes("US-ASCII");

            output.write(b_uRiffFcc, 0, b_uRiffFcc.length);
            output.write(int2bytes(0x2c - 8), 0, 4);
            output.write(b_uWaveFcc, 0, b_uWaveFcc.length);
            output.write(b_uFmtFcc, 0, b_uFmtFcc.length);

            output.write(int2bytes(16), 0, 4);
            output.write(int2bytes(encodeMode), 0, 2);
            output.write(int2bytes(channel), 0, 2);
            output.write(int2bytes(sampleRate), 0, 4);
            output.write(int2bytes(sampleRate * channel * (nBitsPerSample / 8)), 0, 4);
            output.write(int2bytes(channel * (nBitsPerSample / 8)), 0, 2);
            output.write(int2bytes(nBitsPerSample), 0, 2);
            output.write(b_uDataFcc, 0, b_uDataFcc.length);
            output.write(int2bytes(0), 0, 4);
        } catch (Exception e) {
            Log.w("xxx", "write Wav head fail", e);
        }
    }

    public static void adjustFileSize(String fileName, int recordSize) {
        try {
            RandomAccessFile f = new RandomAccessFile(new File(fileName), "rw");
            long pos1 = 4;
            long pos2 = 40;
            f.seek(pos1);
            f.write(int2bytes(recordSize + 0x2c - 8), 0, 4);
            f.seek(pos2);
            f.write(int2bytes(recordSize), 0, 4);
            f.close();
        } catch (Exception e) {
            Log.w("xxx", "adjustFileSize error:", e);
        }
    }

    private static byte[] int2bytes(int i) {
        byte[] x = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(i).array();
        //Log.i("xxx",String.format("4 0x%02x%02x%02x%02x",x[0],x[1],x[2],x[3]));
        return x;
    }
}
