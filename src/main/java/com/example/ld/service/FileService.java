package com.example.ld.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Service;

/**
 * @author HeYiyu
 * @date 2023/2/11
 */
@Service
public class FileService {
    /**
     * 格式化文件路径
     *
     * @param path 文件路径
     * @return 结尾不带/的路径
     */
    public String formatPath(String path) {
        path = FileUtil.normalize("/" + path);
        if (path.length() > 1 && StrUtil.endWith(path, "/")) {
            path = StrUtil.subBefore(path, "/", true);
        }
        return path;
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
