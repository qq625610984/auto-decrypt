package com.example.ld.service;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.example.ld.pojo.MonitorTask;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

/**
 * @author HeYiyu
 * @date 2020/8/19
 */
@Slf4j
public class FileListener implements FileAlterationListener {
    private final TaskService taskService = SpringUtil.getBean(TaskService.class);
    private final FileService fileService = SpringUtil.getBean(FileService.class);
    private final MonitorTask monitorTask;

    public FileListener(MonitorTask monitorTask) {
        this.monitorTask = monitorTask;
    }

    @Override
    public void onDirectoryCreate(File directory) {}

    @Override
    public void onDirectoryDelete(File directory) {}

    @Override
    public void onFileCreate(File file) {
        decryptFile(file, "创建文件：" + file.getAbsolutePath());
    }

    @Override
    public void onFileChange(File file) {}

    @Override
    public void onFileDelete(File file) {}

    @Override
    public void onDirectoryChange(File directory) {}

    @Override
    public void onStart(FileAlterationObserver observer) {}

    @Override
    public void onStop(FileAlterationObserver observer) {}

    @SneakyThrows
    private void decryptFile(File file, String message) {
        BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        FileTime lastAccessTime = basicFileAttributes.lastAccessTime();
        if (lastAccessTime.toMillis() > monitorTask.getTriggerTime() && !StrUtil.equals(FileUtil.getSuffix(file), "tmp") && !file.isHidden()) {
            try {
                log.debug("文件镜像-{}，{}", message, monitorTask);

            } catch (Exception e) {
                log.error("文件镜像失败-{}，失败信息：{}，{}", message, e.getMessage(), monitorTask);
                log.error(ExceptionUtil.stacktraceToString(e));
            }
        }
    }
}
