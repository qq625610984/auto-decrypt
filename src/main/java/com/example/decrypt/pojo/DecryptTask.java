package com.example.decrypt.pojo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

/**
 * @author HeYiyu
 * @date 2023/2/11
 */
@Data
public class DecryptTask {
    @NotEmpty(message = "serverHost不能为空")
    private String serverHost;
    @Positive(message = "serverPort必填且大于0")
    private int serverPort;
    @NotEmpty(message = "fromPath不能为空")
    private String fromPath;
    @NotEmpty(message = "toPath不能为空")
    private String toPath;
}
