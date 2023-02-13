package com.example.ld.controller;

import com.example.ld.common.result.CommonResult;
import com.example.ld.config.CustomConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author HeYiyu
 * @date 2023/2/12
 */
@RestController
public class ConfigController {
    @Resource
    private CustomConfig customConfig;

    @GetMapping("/config")
    public CommonResult<CustomConfig> getCustomProperties() {
        return CommonResult.success(customConfig);
    }
}
