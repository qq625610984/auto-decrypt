package com.example.decrypt.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.system.SystemUtil;
import com.example.decrypt.common.CommonConstant;
import com.example.decrypt.common.exception.CommonException;
import com.example.decrypt.common.result.CommonResult;
import com.example.decrypt.config.CustomConfig;
import com.example.decrypt.netty.NettyClient;
import com.example.decrypt.util.JacksonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

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
    @Value("${server.port}")
    private int port;

    @Override
    public void run(ApplicationArguments args) {
        List<File> files = FileUtil.loopFiles(new File("C:\\Users"), 1, file -> !file.isHidden() && !file.isFile() && !StrUtil.containsAny(file.getName(), "All", "Default", "Public"));
        String userHome = files.get(0).getAbsolutePath();
        customConfig.setLocalhost(NetUtil.getLocalhostStr());
        customConfig.setCacheTime(customConfig.getCacheTime() * 1000);
        List<String> monitorPath = customConfig.getMonitorPath();
        monitorPath.replaceAll(path -> fileService.formatDirPath(StrUtil.replace(path, "~", userHome)));
        if (customConfig.isProbe()) {
            try {
                if (!StrUtil.isEmpty(customConfig.getServerHost())) {
                    nettyClient.connect(customConfig.getServerHost(), customConfig.getServerPort());
                }
            } catch (Exception e) {
                log.warn(e.getMessage());
            } finally {
                probe();
            }
        }
        log.info(String.valueOf(customConfig));
        // 清空远程解密产生的临时文件
        FileUtil.del(fileService.splicePath(SystemUtil.get(SystemUtil.USER_DIR), CommonConstant.SERVER_DIR));
        if (!StrUtil.isEmpty(customConfig.getServerHost())) {
            log.info("解密服务器：{}:{}", customConfig.getServerHost(), customConfig.getServerPort());
            nettyClient.connect(customConfig.getServerHost(), customConfig.getServerPort());
        } else if (customConfig.isProbe()) {
            throw new CommonException("未探测到可用服务器");
        }
        taskService.initMonitorTask();
    }

    @SneakyThrows
    private void probe() {
        Set<String> onlineList = listOnlineIp();
        CopyOnWriteArrayList<String> usableList = new CopyOnWriteArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(onlineList.size());
        onlineList.forEach(ip -> ThreadUtil.execute(() -> {
            try {
                HttpResponse httpResponse = HttpRequest.get(ip + ":" + port + "/config").timeout(customConfig.getTimeout()).execute();
                CommonResult<Object> result = JacksonUtil.toObject(httpResponse.bodyBytes(), new TypeReference<CommonResult<Object>>() {});
                CustomConfig data = BeanUtil.toBean(result.getData(), CustomConfig.class);
                if (StrUtil.isEmpty(data.getServerHost()) && !StrUtil.equals(data.getLocalhost(), NetUtil.getLocalhostStr())) {
                    customConfig.setServerHost(ip);
                    customConfig.setServerPort(data.getNettyPort());
                    usableList.add(ip);
                }
            } catch (Exception ignore) {
            } finally {
                countDownLatch.countDown();
            }
        }));
        countDownLatch.await();
        log.info("可用服务器地址：{}", usableList);
    }

    private Set<String> listOnlineIp() {
        Set<String> ipSet = new LinkedHashSet<>();
        LinkedHashSet<String> ipv4s = NetUtil.localIpv4s();
        ipv4s.remove("127.0.0.1");
        for (String ipv4 : ipv4s) {
            List<String> split = StrUtil.split(ipv4, '.');
            split.remove(3);
            String prefix = StrUtil.join(".", split);
            for (int i = 1; i <= 255; i++) {
                ipSet.add(prefix + "." + i);
            }
            ipSet.remove(ipv4);
        }
        return ipSet;
    }
}
