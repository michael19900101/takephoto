package com.michael.takephoto.util;

/**
 * Created by Administrator on 2018/1/7.
 */

public class FileManager {
    /**
     * 缓存拍照图片
     *
     * @param filename
     * @return
     */
    public static boolean saveImage(String filename, byte[] image) {
        FileOperation fp = null;
        try {
            // 先删除
            fp = new FileOperation("Images", ".jpeg");
            fp.initFile(filename);
            fp.deleteFile();

            // 新建
            fp = new FileOperation("Images", ".jpeg");
            fp.initFile(filename);
            fp.addLine(image);
            fp.closeFile();

        } catch (OutOfMemoryError error) {
            error.printStackTrace();
            if (null != fp) {
                fp.deleteFile();
            }
            return false;
        } catch (Exception e) {
            fp.closeFile();
            e.printStackTrace();
            return false;
        } finally {
            if (null != fp) {
                fp.closeFile();
            }
        }
        return true;
    }
}
