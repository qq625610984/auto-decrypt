package com.example.ld.pojo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * @author HeYiyu
 * @date 2020/8/7
 */
@Data
public class DecryptTask {
    @NotBlank(message = "toHost不能为空")
    private String toHost;

    @NotEmpty(message = "fromPath不能为空")
    private String fromPath;

    @NotBlank(message = "toPath不能为空")
    private String toPath;

    // 最大重试次数
    private int maxRetryTimes = -1;

    // 是否清理源文件
    private boolean cleanUp;

    // 是否校验MD5值
    private boolean check;

    private boolean autoDecrypt;
}
