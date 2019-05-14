package com.managesystem.bgsystem.Utils;



public class FileUploadUtil {


    public final static String getFileType(String filename) {
        int pos = filename.indexOf(".");
        /*  想tar.gz 这个的文件的后缀获取会出错 */
        return filename.substring(pos);
    }


}
