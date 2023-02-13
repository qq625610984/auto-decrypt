package com.example.ld.pojo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author HeYiyu
 * @date 2023/2/11
 */
@Data
public class MonitorTask {
    @NotBlank(message = "monitorPath不能为空")
    private String monitorPath;
    private long triggerTime;
}
