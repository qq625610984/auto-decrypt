package com.example.ld.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.example.ld.config.CustomProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author HeYiyu
 * @date 2020/8/19
 */
@Slf4j
@Service
public class AutoRunService implements ApplicationRunner {
    @Resource
    private CustomProperties customProperties;
    @Resource
    private TaskService taskService;

    @Override
    public void run(ApplicationArguments args) {
        List<String> monitorPath = customProperties.getMonitorPath();
        monitorPath.replaceAll(path -> FileUtil.normalize(StrUtil.replace(path, "{Home}", SystemUtil.getUserInfo().getHomeDir())));
        log.info(String.valueOf(customProperties));
        taskService.initMonitorTask();
    }
}
