package com.example.decrypt.pojo;

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
    private long sendBackLength;
    private long totalBackLength;
    private String errorMessage;

    public void updateSendLength(int sendLength) {
        this.sendLength += sendLength;
    }

    public void updateSendBackLength(int sendLength) {
        this.sendBackLength += sendLength;
    }
}
