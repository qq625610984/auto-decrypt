package com.example.ld.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.example.ld.config.CustomProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author HeYiyu
 * @date 2020/8/19
 */
@Slf4j
@Service
public class AutoRunService implements ApplicationRunner {
    @Resource
    private CustomProperties customProperties;

    @Override
    public void run(ApplicationArguments args) {
        // 修正本机IP地址
        String localhost = customProperties.getLocalhost();
        if (!isValid(localhost) || (NetUtil.isInnerIP(localhost) && !NetUtil.localIpv4s().contains(localhost))) {
            localhost = NetUtil.getLocalhostStr();
            customProperties.setLocalhost(localhost);
            log.warn("配置修改，localhost被修正为：{}", localhost);
        }
        List<String> monitorPath = customProperties.getMonitorPath();
        monitorPath.replaceAll(path -> FileUtil.normalize(StrUtil.replace(path, "{Home}", SystemUtil.getUserInfo().getHomeDir())));
        log.info(String.valueOf(customProperties));
        for (String path : monitorPath) {
            if (!FileUtil.exist(path)) {
                log.info("创建文件夹：{}", FileUtil.mkdir(path).getAbsolutePath());
            }

        }
    }

    /**
     * 判断节点host的合法性
     * 1. host不能为域名
     * 2. host不能是环路地址或本网络地址
     *
     * @param host 节点IP
     * @return 是否合法
     */
    private boolean isValid(String host) {
        try {
            List<String> split = StrUtil.split(host, ".");
            for (String each : split) {
                int parseInt = Integer.parseInt(each);
                if (parseInt < 0 || parseInt > 255) {
                    return false;
                }
            }
            return split.size() == 4 && !host.equals("127.0.0.1") && !host.equals("0.0.0.0");
        } catch (Exception e) {
            return false;
        }
    }
}
