package com.example.ld.common;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;

import java.util.List;

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
    // 重试延迟时间
    public static final List<Integer> DELAY = CollUtil.newArrayList(10, 20, 30, 60);
    // 临时文件后缀名
    public static final String tempSuffix = ".tmp";
}
