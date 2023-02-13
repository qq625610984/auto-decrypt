package com.example.ld.common;

import lombok.Data;

/**
 * @author HeYiyu
 * @date 2023/2/12
 */
@Data
public class CommonConstant {
    // 文件传输每次发送数据片大小
    public static final int DATA_SLICE = 65536;
    // 每轮文件传输发送的数据片数量
    public static final int TRANSMIT_TIMES = 100;
    // 临时文件后缀名
    public static final String TEMP_SUFFIX = ".tmp";
    // 远程解密文件的临时保存位置
    public static final String SERVER_DIR = "Encrypt";
    // Netty传输确认信号
    public static final String ACK = "ack";
}
