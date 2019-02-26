package com.tencent.androidvprocessor;

public class VProcessorHelper {
    public native String simdInstructionSets();
    public native void writeWavFile(String filename, int num_samples, int num_channels, short[] wav_data);
}
