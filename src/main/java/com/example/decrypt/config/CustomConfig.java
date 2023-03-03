package com.example.decrypt.config;

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
    private int nettyPort;
    private int serverPort;
    private String serverHost;
    private boolean probe;
    private int timeout = 500;
    private String refreshFlag = "aaa";
    private long cacheTime = 2;
    private int monitorStartDay;
    private long monitorInterval;
    private List<String> monitorPath = new ArrayList<>();
    private String localhost;
}
