package com.konsung.util;

/**
 * Created by Administrator on 2016/4/25 0025.
 */

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件工具类
 */
public class FileUtils {
    /**
     * 获取程序外部(sd)的目录路径
     * @param context 上下文
     * @return sd卡路径
     */
    public static String getSdPath(Context context) {
        return Environment.getDataDirectory().getAbsolutePath();
    }
    /**
     * 获取程序外部(sd)的目录路径
     * @param context 上下文
     * @return sd卡路径
     */
    public static String getSdLogPath(Context context) {
        return  Environment.getExternalStorageDirectory()+"";
    }
    /**
     * 获取程序文件目录路径
     * @param context 上下文
     * @return 文件路径
     */
    public static String getFilePath(Context context) {
        final String cacheDir = "/" + context.getPackageName()
                + "/";
        return context.getFilesDir().getAbsolutePath() + cacheDir;
    }

    /**
     * 创建文件夹
     * @param file 路径
     */
    public static void mkdir(String file) {
        File dir = new File(file);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 创建文件夹
     * @param path 路径
     * @return true 文件存在 false 不存在
     */
    public static boolean isFileExist(String path) {
        File file = new File(path);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    /**
     * 检测是否有sd卡
     * @return boolen值
     */
    public static boolean existSDCard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * sd卡剩余容量
     * @return 单位MB
     */
    public static long getSDFreeSize() {
        // 取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        // 空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        // 返回SD卡空闲大小
        // return freeBlocks * blockSize; //单位Byte
        // return (freeBlocks * blockSize)/1024; //单位KB
        return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    /**
     * 创建下一级文件
     * @param parentFile 父文件
     * @param childFileName 子文件名
     * @return 如果文件存在return false,否则return true;
     */
    public static boolean createFile(File parentFile, String childFileName) {
        String path = parentFile.getAbsolutePath() + File.separator
                + childFileName + File.separator;
        File tempFile = new File(path);
        if (tempFile.exists()) {
            return false;
        } else {
            tempFile.mkdirs();
            return true;
        }
    }

    /**
     * 复制单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     */
    public static void copyFile(String oldPath, String newPath) {
        if (!oldPath.endsWith("/")) {
            oldPath = oldPath + "/";
        }
        InputStream inStream = null;
        FileOutputStream fs = null;
        try {
            int bytesum = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { // 文件存在时
                inStream = new FileInputStream(oldPath); // 读入原文件
                fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                int byteread = 0;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        } finally {

            try {
                if (inStream != null) {
                    inStream.close();
                }
                if (fs != null) {
                    fs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 遍历sd卡的文件
     * @param path 文件路径
     * @return 所有文件
     */
    public static List<String> getAllFiles(String path) {
        File file = new File(path);
        File[] files = null;
        List<String> list = new ArrayList<>();
        if (file.isDirectory()) {
            files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    list.add(f.getAbsolutePath());
                }
            }
        } else {
        }
        return list;
    }

    /**
     * 删除file
     * @param file 文件
     */
    public static void deleteFile(File file) {

        if (!file.exists()) {
            return;
        }

        File[] fileArray = file.listFiles();

        if (fileArray.length > 0) {
            for (int i = 0; i < fileArray.length; i++) {
                if (fileArray[i].isFile()) {
                    if (fileArray[i].delete()) {
                        System.out.println("删除成功");
                    } else {
                        System.out.println("删除不成功");
                    }
                } else {
                    deleteFile(fileArray[i]);
                }
            }
        }

        file.delete();
    }

    /**
     * @param bis 缓冲输入流
     * @param bos 缓冲输出流
     * @throws IOException 异常
     */
    public static void copy(InputStream bis, FileOutputStream bos) throws IOException {
        byte[] buf = new byte[1024];
        int len = -1;
        while (((len = bis.read(buf)) != -1) && (len > 0)) {
            bos.write(buf, 0, len);
        }
        bis.close();
        bos.close();
    }

    /**
     * 复制asset文件到指定目录
     * @param oldPath asset下的路径
     * @param newPath SD卡下保存路径
     * @param context 上下文
     */
    public static void copyAssets(Context context, String oldPath, String newPath) {
        try {
            // 获取assets目录下的所有文件及目录名
            String[] fileNames = context.getAssets().list(oldPath);
            if (fileNames.length > 0) {
                // 新建sd新的目录
                File file = new File(newPath);
                if (!file.isDirectory()) {

                    file.mkdirs(); // 如果文件夹不存在，则递归
                }
                for (String fileName : fileNames) {
                    copyAssets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else { // 如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) { // 循环从输入流读取
                    // buffer字节
                    fos.write(buffer, 0, byteCount); // 将读取的输入流写入到输出流
                }
                fos.flush(); // 刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
