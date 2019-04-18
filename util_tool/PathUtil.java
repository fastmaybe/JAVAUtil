package com.secmask.util.tool;


import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLDecoder;
/**
 * @author wd
 * @Program DBMaskerServer
 * @create 2018-08-24 16:03
 */
public class PathUtil {
    private static String rootPath = "";

    private PathUtil() {
        init();
    }

    @SuppressWarnings("deprecation")
    private static void init() {
        String path = null;
        File file = null;
        try {
            path = ResourceUtils.getURL("classpath:").getPath();
            file = new File(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(file != null) {
            path = new File(file.getParent()).getParent();
        }
        rootPath = path;
    }

    /**
     * 获取应用的根目录，路径分隔符为/，路径结尾无/
     *
     * @return
     */
    public static String getProjectPath() {
        if (rootPath==null||"".equals(rootPath)) {
            init();
        }
        return rootPath;
    }

}
