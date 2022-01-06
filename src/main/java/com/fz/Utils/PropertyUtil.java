package com.fz.Utils;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

import java.io.*;
import java.util.Properties;
import java.util.Set;

public class PropertyUtil {

    private static final MyProperties prop = new MyProperties();

    private static final String propertiesPath = "src/main/java/com/fz/config/personnelInformation.properties";


    public static void main(String[] args) {
        JSONArray array = TraversePropertiesInfo();
        System.out.println("全部结果:" + array);
    }

    /**
     * 根据key读取value
     *
     * @param keyWord 键
     * @return String
     * @Description: 使用缓冲输入流读取配置文件，然后将其加载，再按需操作
     * 绝对路径或相对路径， 如果是相对路径，则从当前项目下的目录开始计算，
     * 如：当前项目路径/config/config.properties,
     * 相对路径就是config/config.properties
     */
    public static String getProperties(String keyWord) {
        Properties prop = getProp();
        return prop.getProperty(keyWord);
    }

    /**
     * 遍历Properties数据,归纳人员信息
     * @return 人员信息
     */
    public static JSONArray TraversePropertiesInfo() {
        JSONArray array = new JSONArray();
        JSONObject obj = new JSONObject();
        Properties prop = getProp();
        boolean flag = false;
        //遍历循环prop
        Set<Object> keys = prop.keySet();
        for (Object key : keys) {
            if (flag) {
                obj = new JSONObject();
                flag = false;
            }
            obj.set(String.valueOf(key), getProperties(String.valueOf(key)));
            if (String.valueOf(key).contains("type")) {
                array.add(obj);
                flag = true;
            }
        }
        return array;
    }

    /**
     * 获取Properties信息
     * @return Properties信息
     */
    private static Properties getProp() {
        try {
            // 通过输入缓冲流进行读取配置文件
            InputStream InputStream = new BufferedInputStream(new FileInputStream(new File(propertiesPath)));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(InputStream, "utf-8"));
            // 加载输入流
            prop.load(bufferedReader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prop;
    }
}
