package com.wst;

import okhttp3.*;
import org.json.JSONObject;

import java.io.File;

public class FileUploader {

    /**
     * 上传文件到指定的 URL，并提取响应中的 data 字段
     * @param file 要上传的文件对象
     * @param url 上传的目标 URL
     * @return 响应中 data 字段的值（即链接）
     * @throws Exception 如果发生错误
     */


    public static void main(String[] args) {
        String a="38元";
        a=a.replaceAll("[^0-9.]", "");
        double b=Double.parseDouble(a);
        System.out.println(b);
    }
}



