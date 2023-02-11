package com.example.ld.config;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author HeYiyu
 * @date 2023/2/10
 */
@Configuration
public class FileMonitorConfig {
    @Resource
    private CustomProperties customProperties;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public FileAlterationMonitor fileAlterationMonitor() {
        return new FileAlterationMonitor(customProperties.getMonitorInterval());
    }
}
