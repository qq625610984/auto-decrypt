package com.example.ld.controller;

import com.example.ld.common.result.CommonResult;
import com.example.ld.config.CustomProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author HeYiyu
 * @date 2023/2/12
 */
@RestController
public class ConfigController {
    @Resource
    private CustomProperties customProperties;

    @GetMapping("/config")
    public CommonResult<CustomProperties> getCustomProperties() {
        return CommonResult.success(customProperties);
    }

    @PutMapping("/config/server")
    public CommonResult<String> modifyServer(@RequestParam String serverHost, @RequestParam int serverPort) {
        customProperties.setServerHost(serverHost);
        customProperties.setServerPort(serverPort);
        return CommonResult.success();
    }
}
