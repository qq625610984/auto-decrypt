package com.example.ld.controller;

import com.example.ld.common.result.CommonResult;
import com.example.ld.pojo.MonitorTask;
import com.example.ld.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author HeYiyu
 * @date 2020/6/17
 */
@Slf4j
@RestController
public class TaskController {
    @Resource
    private TaskService taskService;

    @PostMapping("/monitor")
    public CommonResult<String> addMonitorTask(@RequestBody MonitorTask monitorTask) {
        taskService.addMonitorTask(monitorTask);
        return CommonResult.success();
    }

    @DeleteMapping("/monitor")
    public CommonResult<String> clearMonitorTask() {
        taskService.clearMonitorTask();
        return CommonResult.success();
    }

    @PutMapping("/monitor")
    public CommonResult<String> resetMonitorTask() {
        taskService.clearMonitorTask();
        taskService.initMonitorTask();
        return CommonResult.success();
    }
}