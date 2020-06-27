package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StaticResourceUtil {


    /**
     * 获取资源绝对路径
     *
     * @param path
     * @return
     */
    public static String getAbsolutePath(String path){

        String absolutePath = StaticResourceUtil.class.getResource("/").getPath();

        return absolutePath.replaceAll("\\\\","/") + path;

    }

    public static void outputStaticResource(InputStream inputStream, OutputStream outputStream) throws IOException {


        int count = 0;
        while (count == 0){
            count = inputStream.available();
        }

        // 输出http请求头,然后再输出具体内容
        outputStream.write(HttpProtocolUtil.getHttpHeader200(count).getBytes());

        long written = 0;  // 已经读取的内容长度
        int byteSize = 1024; // 计划每次缓冲的长度
        byte[] bytes = new byte[byteSize];

        while (written < count){
            // 剩余不足1024
            if(written + byteSize > count){

                byteSize = (int) (count - written);  // 剩余的文件内容长度
                bytes = new byte[byteSize];
            }
            inputStream.read(bytes);
            outputStream.write(bytes);

            outputStream.flush();
            written+=byteSize;
        }
    }



}
