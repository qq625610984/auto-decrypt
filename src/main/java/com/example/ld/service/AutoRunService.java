package com.example.ld.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.Ipv4Util;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.example.ld.common.CommonConstant;
import com.example.ld.common.result.CommonResult;
import com.example.ld.config.CustomConfig;
import com.example.ld.netty.NettyClient;
import com.example.ld.util.JacksonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

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
    @Resource
    private ExecutorService threadPool;
    @Value("${server.port}")
    private int port;

    @SneakyThrows
    @Override
    public void run(ApplicationArguments args) {
        List<String> monitorPath = customConfig.getMonitorPath();
        monitorPath.replaceAll(path -> fileService.formatDirPath(path));
        if (customConfig.isProbe() && StrUtil.isEmpty(customConfig.getServerHost())) {
            List<String> ipList = Ipv4Util.list(NetUtil.getLocalhostStr() + "/24", false);
            CopyOnWriteArrayList<String> onlineList = new CopyOnWriteArrayList<>();
            CountDownLatch countDownLatch = new CountDownLatch(ipList.size());
            ipList.forEach(ip -> threadPool.execute(() -> {
                try {
                    HttpResponse httpResponse = HttpRequest.get(ip + ":" + port + "/config").timeout(100).execute();
                    CommonResult<CustomConfig> result = JacksonUtil.toObject(httpResponse.bodyBytes(), new TypeReference<CommonResult<CustomConfig>>() {});
                    CustomConfig data = result.getData();
                    if (StrUtil.isEmpty(data.getServerHost()) && !StrUtil.equals(ip, NetUtil.getLocalhostStr())) {
                        customConfig.setServerHost(ip);
                        customConfig.setServerPort(data.getNettyPort());
                        onlineList.add(ip);
                    }
                } catch (Exception ignore) {
                } finally {
                    countDownLatch.countDown();
                }
            }));
            countDownLatch.await();
            log.info(onlineList.toString());
        }
        log.info(String.valueOf(customConfig));
        // 清空远程解密产生的临时文件
        FileUtil.del(fileService.splicePath("~", CommonConstant.SERVER_DIR));
        if (!StrUtil.isEmpty(customConfig.getServerHost())) {
            nettyClient.connect(customConfig.getServerHost(), customConfig.getNettyPort());
        }
        taskService.initMonitorTask();
    }
}
