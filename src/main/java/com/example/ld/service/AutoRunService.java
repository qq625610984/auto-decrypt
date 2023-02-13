package com.example.ld.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.example.ld.common.CommonConstant;
import com.example.ld.config.CustomConfig;
import com.example.ld.netty.NettyClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author HeYiyu
 * @date 2023/2/11
 */
@Slf4j
@Service
public class AutoRunService implements ApplicationRunner {
    @Resource
    private CustomConfig customConfig;
    @Resource
    private TaskService taskService;
    @Resource
    private FileService fileService;
    @Resource
    private NettyClient nettyClient;

    @Override
    public void run(ApplicationArguments args) {
        List<String> monitorPath = customConfig.getMonitorPath();
        monitorPath.replaceAll(path -> fileService.formatDirPath(path));
        log.info(String.valueOf(customConfig));
        // 清空远程解密产生的临时文件
        FileUtil.del(CommonConstant.SERVER_DIR);
        if (!StrUtil.isEmpty(customConfig.getServerHost())) {
            nettyClient.connect(customConfig.getServerHost(), customConfig.getNettyPort());
        }
        taskService.initMonitorTask();
    }
}
