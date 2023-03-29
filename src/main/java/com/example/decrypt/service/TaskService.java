package com.example.decrypt.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.example.decrypt.common.CommonConstant;
import com.example.decrypt.config.CustomConfig;
import com.example.decrypt.netty.NettyClient;
import com.example.decrypt.pojo.DecryptTask;
import com.example.decrypt.pojo.MonitorTask;
import com.example.decrypt.pojo.TaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;
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
    private CustomConfig customConfig;
    @Resource
    private FileService fileService;
    @Resource
    private FileAlterationMonitor fileAlterationMonitor;
    @Resource
    private NettyClient nettyClient;

    private final Map<String, FileAlterationObserver> fileObserverMap = new ConcurrentHashMap<>();

    public void addDecryptTask(DecryptTask decryptTask) {
        List<File> fileList = FileUtil.loopFiles(decryptTask.getFromPath(), file -> !file.isHidden() && !StrUtil.endWith(file.getName(), CommonConstant.TEMP_SUFFIX));
        for (File file : fileList) {
            ThreadUtil.execute(() -> {
                try {
                    log.debug("开始解密-{}", file.getAbsolutePath());
                    if (StrUtil.isEmpty(decryptTask.getServerHost())) {
                        // 本地解密
                        File tempFile = fileService.getTempFile(file);
                        FileUtil.copy(file, tempFile, true);
                        handleDecryptFile(file, tempFile);
                    } else {
                        // 远程解密
                        TaskInfo taskInfo = new TaskInfo();
                        taskInfo.setTaskId(IdUtil.objectId());
                        taskInfo.setServerHost(decryptTask.getServerHost());
                        taskInfo.setServerPort(decryptTask.getServerPort());
                        taskInfo.setFromPath(FileUtil.normalize(file.getAbsolutePath()));
                        taskInfo.setToPath(fileService.splicePath(decryptTask.getToPath(), taskInfo.getTaskId() + CommonConstant.TEMP_SUFFIX));
                        taskInfo.setTotalLength(file.length());
                        nettyClient.send(taskInfo);
                    }
                } catch (Exception e) {
                    log.error("解密失败-{}，失败信息：{}", file.getAbsolutePath(), e.getMessage());
                    log.error(ExceptionUtil.stacktraceToString(e));
                }
            });
        }
    }

    public void handleDecryptFile(File file, File tempFile) {
        try {
            FileUtil.del(file);
            FileUtil.rename(tempFile, file.getName(), true);
        } catch (Exception ignore) {
            String path = StrUtil.removeSuffix(FileUtil.getAbsolutePath(file), file.getName());
            String dir = path + CommonConstant.DECRYPT_DIR + DateUtil.format(new Date(), "MMdd-HHmm") + "\\";
            FileUtil.move(tempFile, FileUtil.mkdir(dir), true);
            tempFile = new File(StrUtil.replace(tempFile.getAbsolutePath(), path, dir));
            FileUtil.rename(tempFile, file.getName(), true);
        }
    }

    public void addMonitorTask(MonitorTask monitorTask) {
        monitorTask.setMonitorPath(fileService.formatDirPath(monitorTask.getMonitorPath()));
        if (monitorTask.getTriggerTime() == 0) {
            monitorTask.setTriggerTime(DateUtil.offsetHour(new Date(), customConfig.getMonitorStartDay() * -1).getTime());
        }
        File file = new File(monitorTask.getMonitorPath());
        if (!fileObserverMap.containsKey(file.getAbsolutePath())) {
            log.info("监控文件夹：{}", FileUtil.mkdir(file).getAbsolutePath());
            // 清理临时存放文件夹
            List<File> files = fileService.loopFiles(file, File::isDirectory);
            for (File each : files) {
                if (ReUtil.isMatch(CommonConstant.DECRYPT_DIR + "\\d{4}-\\d{4}", each.getName())) {
                    FileUtil.del(each);
                }
            }
            // 清理临时文件
            FileUtil.loopFiles(file.getAbsolutePath(), each -> StrUtil.endWith(each.getName(), CommonConstant.TEMP_SUFFIX)).forEach(FileUtil::del);
            FileAlterationObserver fileObserver = new FileAlterationObserver(file);
            fileObserver.addListener(new FileListener(monitorTask, customConfig, this));
            fileObserverMap.put(file.getAbsolutePath(), fileObserver);
            fileAlterationMonitor.addObserver(fileObserver);
        }
    }

    public void initMonitorTask() {
        customConfig.getMonitorPath().forEach(path -> {
            MonitorTask monitorTask = new MonitorTask();
            monitorTask.setMonitorPath(path);
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
