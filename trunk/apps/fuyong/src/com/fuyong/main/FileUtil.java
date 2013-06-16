package com.fuyong.main;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-6-10
 * Time: 下午11:47
 * To change this template use File | Settings | File Templates.
 */
public class FileUtil {
    private Logger logger = LogManager.getLogger(FileUtil.class);

    public static boolean createNewFile(String filePath) throws IOException {
        File file = new File(filePath.substring(0,filePath.lastIndexOf(File.separator)));
        file.mkdirs();
        return new File(filePath).createNewFile();
    }

    public static void copyFile(InputStream inputStream, OutputStream outputStream) throws IOException {
        if (null == inputStream || null == outputStream) {
            return;
        }

        BufferedInputStream inFile = null;
        BufferedOutputStream outFile = null;
        try {
            inFile = new BufferedInputStream(inputStream);
            outFile = new BufferedOutputStream(outputStream);
            byte[] buff = new byte[1024 * 512];
            int len;
            while ((len = inFile.read(buff)) != -1) {
                outFile.write(buff, 0, len);
            }
            outFile.flush();
        } finally {
            if (inFile != null)
                inFile.close();
            if (outFile != null)
                outFile.close();
        }
    }

    public static boolean isExit(String sourceFile) {
        return new File(sourceFile).exists();
    }

    // 复制文件
    public static void copyFile(String sourceFile, String targetFile) throws IOException {
        if (!isExit(sourceFile)) {
            return;
        }
        copyFile(new FileInputStream(sourceFile), new FileOutputStream(targetFile));
    }

    // 复制文件夹
    public static void copyDirectory(String sourceDir, String targetDir) throws IOException {
        // 新建目标目录

        (new File(targetDir)).mkdirs();
        // 获取源文件夹当前下的文件或目录
        File[] files = (new File(sourceDir)).listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                copyFile(files[i].getAbsolutePath(), targetDir + File.separator + files[i].getName());
            }
            if (files[i].isDirectory()) {
                String dir1 = sourceDir + File.separator + files[i].getName();
                String dir2 = targetDir + File.separator + files[i].getName();
                copyDirectory(dir1, dir2);
            }
        }
    }
}
