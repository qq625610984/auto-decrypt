package com.example.ld.config;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author HeYiyu
 * @date 2023/2/11
 */
@Configuration
public class FileMonitorConfig {
    @Resource
    private CustomConfig customConfig;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public FileAlterationMonitor fileAlterationMonitor() {
        return new FileAlterationMonitor(customConfig.getMonitorInterval());
    }
}
