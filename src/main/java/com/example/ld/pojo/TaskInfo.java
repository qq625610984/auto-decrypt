package com.example.ld.pojo;

import lombok.Data;

/**
 * @author HeYiyu
 * @date 2020/4/21
 */
@Data
public class TaskInfo {
    // 任务ID
    private String taskId;
    // 发送方地址
    private String fromHost;
    // 发送方端口
    private int fromPort;
    // 源文件所在位置
    private String fromPath;
    // 目标文件的目的保存位置
    private String toPath;
    // 已传输数据量
    private long sendLength;
    // 总数据量
    private long totalLength;
    // 是否清理源文件
    private boolean cleanUp;
    // 自动解密回传
    private boolean autoDecrypt;
    // 错误信息
    private String errorMessage;

    /**
     * 更新已发送文件长度
     *
     * @param sendLength 新增文件长度
     */
    public void addSendLength(int sendLength) {
        this.sendLength += sendLength;
    }
}
