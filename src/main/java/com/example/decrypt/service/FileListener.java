package com.example.decrypt.service;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
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
import java.util.List;

/**
 * @author HeYiyu
 * @date 2023/2/11
 */
@Slf4j
public class FileListener implements FileAlterationListener {
    private final MonitorTask monitorTask;
    private final CustomConfig customConfig;
    private final TaskService taskService;
    private final String toPath;
    private final TimedCache<Object, Object> timedCache;

    public FileListener(MonitorTask monitorTask, CustomConfig customConfig, TaskService taskService) {
        this.monitorTask = monitorTask;
        this.customConfig = customConfig;
        this.taskService = taskService;
        toPath = CommonConstant.SERVER_DIR + "/" + NetUtil.getLocalhostStr();
        timedCache = CacheUtil.newTimedCache(customConfig.getCacheTime());
        timedCache.schedulePrune(1000);
    }

    @Override
    public void onDirectoryCreate(File directory) {
        refresh(directory);
    }

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
        if (file.isHidden() || timedCache.containsKey(file.getAbsolutePath())) {
            return;
        }
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
            timedCache.put(file.getAbsolutePath(), null);
            taskService.addDecryptTask(decryptTask);
        }
    }

    private void refresh(File directory) {
        if (!directory.isHidden() && StrUtil.equals(directory.getName(), customConfig.getRefreshFlag())) {
            FileUtil.del(directory);
            directory = FileUtil.getParent(directory, 1);
            log.info("手动刷新目录：{}", directory.getAbsolutePath());
            List<File> files = FileUtil.loopFiles(directory, File::isFile);
            files.forEach(this::decryptFile);
        }
    }
}
