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
     * @return 结尾不带/的路径
     */
    public String formatDirPath(String dir) {
        dir = FileUtil.normalize(dir);
        if (dir.length() > 1 && StrUtil.endWith(dir, "/")) {
            dir = StrUtil.subBefore(dir, "/", true);
        }
        return dir;
    }

    /**
     * 在源文件结尾添加后缀来生成临时文件
     *
     * @param file 源文件
     * @return 同名临时文件
     */
    public File getTempFile(File file) {
        return new File(file.getAbsolutePath() + CommonConstant.TEMP_SUFFIX);
    }

    /**
     * 将多个值拼接为路径，以'/'分隔
     *
     * @param objects 需要拼接的个元素
     * @return 拼接后的路径
     */
    public String splicePath(Object... objects) {
        String filePath = StrUtil.join("/", objects);
        return FileUtil.normalize(filePath);
    }
}
