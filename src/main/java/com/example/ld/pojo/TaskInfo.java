package com.example.ld.pojo;

import lombok.Data;

/**
 * @author HeYiyu
 * @date 2023/2/11
 */
@Data
public class TaskInfo {
    private String taskId;
    private String serverHost;
    private int serverPort;
    private String fromPath;
    private String toPath;
    private long sendLength;
    private long totalLength;
    private String errorMessage;

    /**
     * 更新已发送文件长度
     *
     * @param sendLength 新增文件长度
     */
    public void updateSendLength(int sendLength) {
        this.sendLength += sendLength;
    }
}
