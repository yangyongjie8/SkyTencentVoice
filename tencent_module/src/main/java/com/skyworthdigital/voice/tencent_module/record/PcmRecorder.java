package com.skyworthdigital.voice.tencent_module.record;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.skyworthdigital.voice.common.IStatus;
import com.skyworthdigital.voice.common.utils.FileUtil;
import com.skyworthdigital.voice.common.utils.Utils;
import com.skyworthdigital.voice.common.utils.WavHeadHelper;
import com.skyworthdigital.voice.dingdang.utils.MLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

public class PcmRecorder extends Thread {
    private static final String TAG = "PcmRecorder";
    private static String RECORD_PATH = "/sdcard/speech";
    private AudioRecord mRecorder = null;
    private int buffersize = -1;
    private byte[] buffer = null;
    private boolean isRunning = false;
    private ReentrantLock lock = new ReentrantLock();

    private int setTrack = 64;
    //private int SET_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int SDK_CHANNEL_OUTPUT = 8;
    private static final int DEFAULT_SAMPLE_RATE = 16 * 1000;
    private static final short DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private RecordListener mRecordListener;
    private static final int MIC_NOT_READY_ERR = 1;
    private static final int RECORDING_ERR = 2;
    private static boolean mSavePcmFile = false;
    private static boolean mSaveWavFile = false;
    private static final int SLEEP_TIME = 64;

    public PcmRecorder(RecordListener listener) {
        mRecordListener = listener;
    }

    @Override
    public synchronized void start() {
        super.start();
        isRunning = true;
    }

    /**
     * 停止线程
     */
    public synchronized boolean stopThread() {
        if (!isRunning()) {
            return false;
        }

        isRunning = false;
        try {
            join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "stopThread : " + e.getMessage());
        }
        Log.e(TAG, "stopThread");
        return true;
    }

    @Override
    public void run() {
        if (Utils.isQ3031Recoder()) {
            Q3031RecoderRun();
        } else {
            originRecoderRun();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public interface RecordListener {
        void onRecord(byte[] buffer, int bufferSize);

        void onError(int code, String desc);
    }

    private byte[] getRealBuff(byte[] buf, int size) throws IOException {
        //前面4个通道数据有效，过滤掉后面四个通道
        // write data
        int frameSize = DEFAULT_AUDIO_FORMAT * SDK_CHANNEL_OUTPUT;
        ByteBuffer sinkBuf = ByteBuffer.allocate(size / 2).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < size; i = i + frameSize) {
            sinkBuf.put(buf, i, DEFAULT_AUDIO_FORMAT * 5/*SET_CHANNELS*/);
        }

        return sinkBuf.array();
    }

    private static byte[] toLittle(byte[] b, int len) {
        byte[] targets = new byte[len];
        for (int i = 0; i < len; i = i + 2) {
            byte tmp = b[i];
            targets[i] = b[i + 1];
            targets[i + 1] = tmp;
        }
        if (len % 2 != 0) {
            targets[len - 1] = b[len - 1];
        }
        return targets;
    }

    public static void copyWcompTable(Context ctx) {
        String newPath = RECORD_PATH + "/tables";
        FileUtil.copyFilesFassets(ctx, "tables", newPath);
    }

    public static void setSavePcmFile(boolean on) {
        mSavePcmFile = on;
    }


    public static void setSaveRavFile(boolean on) {
        mSaveWavFile = on;
    }

    private FileOutputStream savePcmFile(String tag) {
        String pathlittle = RECORD_PATH + "/" + tag + "_last.pcm";
        try {
            if (mSavePcmFile) {
                Log.d(TAG, "path=" + pathlittle);
                File filelittle = new File(pathlittle);
                if (filelittle.exists()) {
                    filelittle.delete();
                }

                filelittle.createNewFile();
                return new FileOutputStream(filelittle, false);
            }
        } catch (IOException e) {
            Log.i(TAG, "未能创建");
            e.printStackTrace();
        }
        return null;
    }

    private FileOutputStream saveWavFile(String tag) {
        String pathwav = RECORD_PATH + "/" + tag + ".wav";
        try {
            File file = new File(RECORD_PATH);
            if (!file.exists()) {
                file.mkdirs();//如果文件夹不存在，则递归
            }
            if (mSaveWavFile) {
                File filewav = new File(pathwav);
                if (filewav.exists()) {
                    filewav.delete();
                }
                filewav.createNewFile();
                FileOutputStream doswav = new FileOutputStream(filewav, false);
                WavHeadHelper.writeHEAD(doswav, 4/*SET_CHANNELS*/, DEFAULT_SAMPLE_RATE,
                        DEFAULT_AUDIO_FORMAT * 8, 1);
                return doswav;
            }
        } catch (IOException e) {
            Log.i(TAG, "未能创建");
            e.printStackTrace();
        }
        return null;
    }


    private void originRecoderRun() {
        MLog.i(TAG, "originRecoderRun record start");
        lock.lock();
        FileOutputStream doswav = null;
        int totalRecordSize = 0;
        String pathwav = "";
        try {
            buffersize = AudioRecord.getMinBufferSize(DEFAULT_SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO, DEFAULT_AUDIO_FORMAT);

            //Log.d(TAG, "buffersize=" + String.valueOf(buffersize));
            if (buffersize < 0) {
                MLog.i(TAG, "originRecoderRun record return < 0");
                return;
            } else if (mRecorder == null) {
                MLog.i(TAG, "originRecoderRun record == null "+buffersize);
                if (buffersize < 4096) {
                    buffersize = 4096;
                }
                if (mSaveWavFile) {
                    MLog.i(TAG, "originRecoderRun saveFile");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");// HH:mm:ss
                    Date date = new Date(System.currentTimeMillis());
                    String rectime = simpleDateFormat.format(date);
                    pathwav = RECORD_PATH + "/" + rectime + ".wav";

                    File file = new File(RECORD_PATH);
                    if (!file.exists()) {
                        file.mkdirs();//如果文件夹不存在，则递归
                    }
                    MLog.i(TAG, "originRecoderRun saveFile ...");
                    if (mSaveWavFile) {
                        File filewav = new File(pathwav);
                        if (filewav.exists()) {
                            filewav.delete();
                        }
                        filewav.createNewFile();
                        doswav = new FileOutputStream(filewav, false);
                        WavHeadHelper.writeHEAD(doswav, 1/*SET_CHANNELS*/, DEFAULT_SAMPLE_RATE,
                                DEFAULT_AUDIO_FORMAT * 8, 1);
                    }
                    totalRecordSize = 0;
                }
                buffer = new byte[buffersize];
                mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                        DEFAULT_SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, DEFAULT_AUDIO_FORMAT,
                        buffersize);
                MLog.i(TAG, "originRecoderRun saveFileEnd");
            }
            if (mRecorder.getState() == AudioRecord.STATE_UNINITIALIZED) {
                MLog.i(TAG, "originRecoderRun record uninitialized");
                Log.e(TAG, "Error: AudioRecord state == STATE_UNINITIALIZED");
                mRecordListener.onError(MIC_NOT_READY_ERR, "mic uninitialized");
                return;
            }
            MLog.i(TAG, "originRecoderRun invoke start record");
            mRecorder.startRecording();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            MLog.i(TAG, "originRecoderRun record unlock");
            lock.unlock();
        }

        while (isRunning) {
            try {
                MLog.i(TAG, "originRecoderRun running");
                int count = mRecorder.read(buffer, 0, buffersize);
                if (count > 0 && null != mRecordListener) {
                    MLog.i(TAG, "originRecoderRun onRecord");
                    mRecordListener.onRecord(buffer, count);
                }
                MLog.i(TAG, "originRecoderRun onRecord end");
                if (mSaveWavFile) {
                    MLog.i(TAG, "originRecoderRun mSaveWavFile");
                    if (doswav != null) {
                        MLog.i(TAG, "originRecoderRun doswav not null");
                        doswav.write(buffer, 0, count);
                        totalRecordSize += buffersize;
                    }
                }
            } catch (Exception e) {
                MLog.i(TAG, "originRecoderRun onError");
                mRecordListener.onError(RECORDING_ERR, e.getMessage());
                e.printStackTrace();
            }
            try {
                Thread.sleep(buffersize / setTrack);
            } catch (InterruptedException e) {
                e.printStackTrace();
                mRecordListener.onError(RECORDING_ERR, e.getMessage());
                Log.e(TAG, "InterruptedException: " + e.getMessage());
            }
        }
        if (mRecorder != null) {
            MLog.i(TAG, "originRecoderRun stop");
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
        if (mSaveWavFile) {
            try {
                if (doswav != null) {
                    doswav.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            WavHeadHelper.adjustFileSize(pathwav, totalRecordSize);
        }
    }

    private void Q3031RecoderRun() {
        lock.lock();
        FileOutputStream doswav = null;
        FileOutputStream dospcm = null;
        int num_channels = 4;
        int totalRecordSize = 0;
        String rectime = "" + System.currentTimeMillis();
        String pathwav = RECORD_PATH + "/" + rectime + ".wav";
        //String pathlittle = RECORD_PATH + rectime + "_little.pcm";
        int SET_CHANNELS = 5;
        Log.e("pcm", "start");
        try {
            buffersize = AudioRecord.getMinBufferSize(DEFAULT_SAMPLE_RATE,
                    SET_CHANNELS, DEFAULT_AUDIO_FORMAT);

            if (buffersize < 0) {
                return;
            } else if (mRecorder == null) {
                final int outputFrameSize = DEFAULT_AUDIO_FORMAT * 8;
                if (buffersize < DEFAULT_SAMPLE_RATE * outputFrameSize / 20) {
                    buffersize = (DEFAULT_SAMPLE_RATE / 20) * outputFrameSize;
                } else if (buffersize % (outputFrameSize) != 0) {
                    buffersize = outputFrameSize * (buffersize / outputFrameSize + 1);
                }

                buffer = new byte[buffersize];
                if (mSavePcmFile) {
                    dospcm = savePcmFile(rectime);
                }
                if (mSaveWavFile) {
                    doswav = saveWavFile(rectime);
                    totalRecordSize = 0;
                }

                mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                        DEFAULT_SAMPLE_RATE, SET_CHANNELS, DEFAULT_AUDIO_FORMAT,
                        buffersize);
            }
            if (mRecorder.getState() == AudioRecord.STATE_UNINITIALIZED) {
                Log.e(TAG, "Error: AudioRecord state == STATE_UNINITIALIZED");
                mRecordListener.onError(MIC_NOT_READY_ERR, "mic uninitialized");
                return;
            }
            mRecorder.startRecording();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        while (isRunning) {
            try {
                int count = mRecorder.read(buffer, 0, buffersize);
                if (count != buffersize) {
                    if (count < 0) {
                        Log.e(TAG, "audio record read error : " + count);
                    } else {
                        Log.w(TAG, "audio record expect " + buffersize + " bytes, but get " + count + " bytes");
                    }
                }

                //byte[] realbuffer = getRealBuff(buffer, buffersize);//8通道过滤出前4个通道数据
                //int realbuffersize = buffersize / 2;//真实长度减半

                if (mSaveWavFile && doswav != null) {
                    doswav.write(buffer, 0, buffersize);
                    totalRecordSize += buffersize;
                }
                short[] input_data = new short[buffersize / 2];

                short[] output_data = new short[buffersize / 2 / num_channels];
                ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(input_data);

                int read_size = buffersize;
                int flag = (IStatus.getDialogSmall()) ? 1 : 0;
                //Log.e(TAG, "flag:" + flag);
//                int processed = mVoiceProcessor.process(input_data, read_size / 2 / num_channels, output_data, buffersize / 2 / num_channels, flag);
//                if (processed > 0) {
//                    ByteBuffer bb = ByteBuffer.allocate(buffersize / num_channels);
//                    bb.asShortBuffer().put(output_data);
//                    if (count > 0 && null != mRecordListener) {
//                        mRecordListener.onRecord(toLittle(bb.array(), buffersize / num_channels)/*bb.array()*/, buffersize / num_channels);
//                    }
//
//                    if (mSavePcmFile && dospcm != null) {
//                        dospcm.write(toLittle(bb.array(), buffersize / num_channels));
//                        dospcm.flush();
//                    }
//                }
            } catch (Exception e) {
                mRecordListener.onError(RECORDING_ERR, e.getMessage());
                e.printStackTrace();
            }
            try {
                Thread.sleep(SLEEP_TIME);//buffersize / setTrack);
            } catch (InterruptedException e) {
                e.printStackTrace();
                mRecordListener.onError(RECORDING_ERR, e.getMessage());
                Log.e(TAG, "InterruptedException: " + e.getMessage());
            }
        }
        Log.e("pcm", "over");
        try {
            if (mSaveWavFile && doswav != null) {
                doswav.close();
                WavHeadHelper.adjustFileSize(pathwav, totalRecordSize);
            }
            if (mSavePcmFile && dospcm != null) {
                dospcm.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }
}
