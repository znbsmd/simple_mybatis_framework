package com.zjn.io;

import java.io.InputStream;

/**
 * @author zjn
 * @create 2020-05-23 09:52
 * @description 文件读入到字节流中
 */
public class Resources {

    // 根据配置文件的路径，将配置文件加载成字节输入流，存储在内存中
    public static InputStream getResourceAsSteam(String path){

        InputStream resourceAsStream = Resources.class.getClassLoader().getResourceAsStream(path);

        return  resourceAsStream;

    }
}
