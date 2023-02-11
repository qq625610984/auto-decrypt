package com.example.ld.pojo;

import lombok.Data;

/**
 * @author HeYiyu
 * @date 2023/2/11
 */
@Data
public class MonitorTask {
    private String monitorPath;
    private long triggerTime;
    private String toHost;
    private int toPort;
    private String toPath;
}
