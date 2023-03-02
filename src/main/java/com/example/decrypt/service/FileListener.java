package com.example.decrypt.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.example.decrypt.common.CommonConstant;
import com.example.decrypt.config.CustomConfig;
import com.example.decrypt.pojo.DecryptTask;
import com.example.decrypt.pojo.MonitorTask;
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
 * @date 2023/2/11
 */
@Slf4j
public class FileListener implements FileAlterationListener {
    private final TaskService taskService = SpringUtil.getBean(TaskService.class);
    private final CustomConfig customConfig = SpringUtil.getBean(CustomConfig.class);
    private final MonitorTask monitorTask;
    private final String toPath;

    public FileListener(MonitorTask monitorTask) {
        this.monitorTask = monitorTask;
        toPath = CommonConstant.SERVER_DIR + "/" + NetUtil.getLocalhostStr();
    }

    @Override
    public void onDirectoryCreate(File directory) {}

    @Override
    public void onDirectoryDelete(File directory) {}

    @Override
    public void onFileCreate(File file) {
        decryptFile(file);
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
    private void decryptFile(File file) {
        if (!file.isHidden()) {
            String suffix = FileUtil.getSuffix(file);
            if (StrUtil.isEmpty(suffix) || StrUtil.equals(suffix, CommonConstant.TEMP_SUFFIX)) {
                return;
            }
            BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            FileTime lastAccessTime = basicFileAttributes.lastAccessTime();
            if (lastAccessTime.toMillis() > monitorTask.getTriggerTime()) {
                DecryptTask decryptTask = new DecryptTask();
                decryptTask.setServerHost(customConfig.getServerHost());
                decryptTask.setServerPort(customConfig.getServerPort());
                decryptTask.setFromPath(file.getAbsolutePath());
                decryptTask.setToPath(toPath);
                taskService.addDecryptTask(decryptTask);
            }
        }
    }
}
