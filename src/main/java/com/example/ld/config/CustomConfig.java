package com.example.ld.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置信息
 *
 * @author HeYiyu
 * @date 2023/2/11
 */
@Data
@Component
@ConfigurationProperties(prefix = "custom")
public class CustomConfig {
    private String serverHost;
    private int serverPort;
    private int nettyPort;
    private int monitorStartDay;
    private long monitorInterval;
    private boolean probe;
    private List<String> monitorPath = new ArrayList<>();
}
