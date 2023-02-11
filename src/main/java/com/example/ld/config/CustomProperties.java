package com.example.ld.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置信息
 *
 * @author HeYiyu
 * @date 2023/1/4
 */
@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "custom")
public class CustomProperties {
    private String serverHost;
    private int serverPort;
    private int nettyPort;
    private String localhost;
    private int monitorStartDay;
    private long monitorInterval;
    private List<String> monitorPath = new ArrayList<>();
}
