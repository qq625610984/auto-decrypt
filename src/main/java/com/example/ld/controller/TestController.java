package com.example.ld.controller;

import com.example.ld.common.result.CommonResult;
import com.example.ld.config.CustomProperties;
import com.example.ld.pojo.MonitorTask;
import com.example.ld.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author HeYiyu
 * @date 2020/6/17
 */
@Slf4j
@RestController
public class TestController {
    @Resource
    private CustomProperties customProperties;
    @Resource
    private TaskService taskService;

    @GetMapping("/config")
    public CommonResult<CustomProperties> getCustomProperties() {
        return CommonResult.success(customProperties);
    }

    @PostMapping("/monitor")
    public CommonResult<String> addMonitorTask(@RequestBody MonitorTask monitorTask) {
        taskService.addMonitorTask(monitorTask);
        return CommonResult.success();
    }
}
