package com.example.ld.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.example.ld.common.CommonConstant;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author HeYiyu
 * @date 2023/2/11
 */
@Service
public class FileService {
    /**
     * 格式化文件夹路径
     *
     * @param dir 文件夹路径
     * @return 结尾带/的路径
     */
    public String formatDirPath(String dir) {
        return FileUtil.normalize(dir + "/");
    }

    /**
     * 在源文件结尾添加后缀
     *
     * @param file 源文件
     * @return 同名临时文件
     */
    public File getTempFile(File file) {
        return new File(file.getAbsolutePath() + CommonConstant.tempSuffix);
    }

    /**
     * 获取文件相对路径
     *
     * @param absolutePath 文件绝对路径
     * @param prefix       文件路径前缀
     * @return 文件相对路径
     */
    public String getRelativePath(String absolutePath, String prefix) {
        return StrUtil.subAfter(FileUtil.normalize(absolutePath), prefix, false);
    }
}
