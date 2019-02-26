package com.tencent.androidvprocessor;

public class VoiceProcessor {
    /**
     * 初始化函数
     *
     * @param num_channels 通道数目
	 * @param num_refs 参考通道数目，默认为最后几通道
     * @param dir 配置文件目录
     * @return true 成功，false 失败
     */
    public native boolean initialize(int num_channels, int num_refs, String dir);

    /**
     * 结束函数
     */
    public native void shutdown();

    /**
     * 处理数据函数
     *
     * @param in_data 输入数据指针
     * @param in_samples 输入数据大小
     * @param out_buffer 输出数据指针
     * @param out_buf_size 输出数据大小
     * @param postFlag 0-正常模式，1-加大回声抑制，尝试在连续识别模式中减小虚警
     * @return -1 失败，其他 实际处理的数据数目
     */
    public native int process(short[] in_data, int in_samples, short[] out_buffer, int out_buf_size, int postFlag);
}
