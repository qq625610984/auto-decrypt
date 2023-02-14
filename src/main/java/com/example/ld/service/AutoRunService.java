package com.example.ld.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.system.SystemUtil;
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
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

    @Override
    public void run(ApplicationArguments args) {
        customConfig.setLocalhost(NetUtil.getLocalhostStr());
        List<String> monitorPath = customConfig.getMonitorPath();
        monitorPath.replaceAll(path -> fileService.formatDirPath(path));
        if (customConfig.isProbe()) {
            try {
                nettyClient.connect(customConfig.getServerHost(), customConfig.getServerPort());
            } catch (Exception e) {
                log.warn(e.getMessage());
                probe();
            }
        }
        log.info(String.valueOf(customConfig));
        // 清空远程解密产生的临时文件
        FileUtil.del(fileService.splicePath("~", CommonConstant.SERVER_DIR));
        if (!StrUtil.isEmpty(customConfig.getServerHost())) {
            nettyClient.connect(customConfig.getServerHost(), customConfig.getServerPort());
        }
        taskService.initMonitorTask();
    }

    @SneakyThrows
    private void probe() {
        List<String> onlineList = listOnlineIp();
        CopyOnWriteArrayList<String> usableList = new CopyOnWriteArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(onlineList.size());
        onlineList.forEach(ip -> threadPool.execute(() -> {
            try {
                HttpResponse httpResponse = HttpRequest.get(ip + ":" + port + "/config").timeout(100).execute();
                CommonResult<CustomConfig> result = JacksonUtil.toObject(httpResponse.bodyBytes(), new TypeReference<CommonResult<CustomConfig>>() {});
                CustomConfig data = result.getData();
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
        log.info(usableList.toString());
    }

    private List<String> listOnlineIp() {
        List<String> ipList = new ArrayList<>();
        if (SystemUtil.getOsInfo().isWindows()) {
            try {
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec("cmd.exe /c arp -a");
                // 输出结果，必须写在 waitFor 之前
                InputStream inputStream = process.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, CharsetUtil.GBK));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (StrUtil.contains(line, "动态")) {
                        ipList.add(StrUtil.subBefore(StrUtil.trimStart(line), " ", false));
                    }
                }
                // 退出值 0 为正常，其他为异常
                process.waitFor();
                process.destroy();
            } catch (Exception ignore) {
            }
        }
        return ipList;
    }
}
