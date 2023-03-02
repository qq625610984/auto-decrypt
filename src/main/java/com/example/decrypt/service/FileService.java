package com.example.decrypt.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.example.decrypt.common.CommonConstant;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

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
     * 递归遍历目录以及子目录中的所有文件
     *
     * @param file       当前遍历文件或目录
     * @param fileFilter 文件过滤规则对象
     * @return 文件列表
     */
    public List<File> loopFiles(File file, FileFilter fileFilter) {
        final List<File> fileList = new ArrayList<>();
        File[] subFiles = file.listFiles();
        if (ArrayUtil.isNotEmpty(subFiles)) {
            for (File each : subFiles) {
                fileList.addAll(loopFiles(each, fileFilter));
            }
        }
        if (fileFilter == null || fileFilter.accept(file)) {
            fileList.add(file);
        }
        return fileList;
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
