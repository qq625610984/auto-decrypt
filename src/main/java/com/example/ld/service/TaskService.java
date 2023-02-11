package com.example.ld.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.example.ld.config.CustomProperties;
import com.example.ld.pojo.MonitorTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;

/**
 * @author HeYiyu
 * @date 2023/2/11
 */
@Slf4j
@Service
public class TaskService {
    @Resource
    private CustomProperties customProperties;
    @Resource
    private FileService fileService;
    @Resource
    private FileAlterationMonitor fileAlterationMonitor;

    public void addMonitorTask(MonitorTask monitorTask) {
        monitorTask.setMonitorPath(fileService.formatPath(monitorTask.getMonitorPath()));
        monitorTask.setToPath(fileService.formatPath(monitorTask.getToPath()));
        if (monitorTask.getTriggerTime() == 0) {
            monitorTask.setTriggerTime(DateUtil.offsetDay(new Date(), -customProperties.getMonitorStartDay()).getTime());
        }
        File file = new File(monitorTask.getMonitorPath());
        FileUtil.mkdir(file);
        log.info("监控文件夹：{}", file.getAbsolutePath());
        FileAlterationObserver fileObserver = new FileAlterationObserver(file);
        fileObserver.addListener(new FileListener(monitorTask));
        fileAlterationMonitor.addObserver(fileObserver);
    }
}
