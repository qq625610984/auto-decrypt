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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

    private final Map<String, FileAlterationObserver> fileObserverMap = new ConcurrentHashMap<>();

    public void addMonitorTask(MonitorTask monitorTask) {
        monitorTask.setMonitorPath(fileService.formatDirPath(monitorTask.getMonitorPath()));
        monitorTask.setToPath(fileService.formatDirPath(monitorTask.getToPath()));
        if (monitorTask.getTriggerTime() == 0) {
            monitorTask.setTriggerTime(DateUtil.offsetDay(new Date(), customProperties.getMonitorStartDay() * -1).getTime());
        }
        File file = new File(monitorTask.getMonitorPath());
        if (!fileObserverMap.containsKey(file.getAbsolutePath())) {
            log.info("监控文件夹：{}", FileUtil.mkdir(file).getAbsolutePath());
            FileAlterationObserver fileObserver = new FileAlterationObserver(file);
            fileObserver.addListener(new FileListener(monitorTask));
            fileObserverMap.put(file.getAbsolutePath(), fileObserver);
            fileAlterationMonitor.addObserver(fileObserver);
        }
    }

    public void initMonitorTask() {
        customProperties.getMonitorPath().forEach(path -> {
            MonitorTask monitorTask = new MonitorTask();
            monitorTask.setMonitorPath(path);
            monitorTask.setToHost(customProperties.getServerHost());
            monitorTask.setToPort(customProperties.getServerPort());
            addMonitorTask(monitorTask);
        });
    }

    public void clearMonitorTask() {
        fileObserverMap.values().forEach(fileObserver -> fileAlterationMonitor.removeObserver(fileObserver));
        fileObserverMap.clear();
    }

    public Set<String> listMonitorTask() {
        return fileObserverMap.keySet();
    }
}
