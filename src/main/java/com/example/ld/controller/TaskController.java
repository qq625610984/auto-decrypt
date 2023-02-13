package com.example.ld.controller;

import com.example.ld.common.result.CommonResult;
import com.example.ld.pojo.DecryptTask;
import com.example.ld.pojo.MonitorTask;
import com.example.ld.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author HeYiyu
 * @date 2023/2/11
 */
@Slf4j
@RestController
public class TaskController {
    @Resource
    private TaskService taskService;

    @PostMapping("/decrypt")
    public CommonResult<String> addDecryptTask(@RequestBody @Validated DecryptTask decryptTask) {
        taskService.addDecryptTask(decryptTask);
        return CommonResult.success();
    }

    @PostMapping("/monitor")
    public CommonResult<String> addMonitorTask(@RequestBody @Validated MonitorTask monitorTask) {
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

    @GetMapping("/monitor")
    public CommonResult<Set<String>> listMonitorTask() {
        return CommonResult.success(taskService.listMonitorTask());
    }
}
