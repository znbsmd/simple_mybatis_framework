package server;

import java.io.IOException;
import java.io.InputStream;

/**
 * 把请求信息封装为Request对象（根据InputSteam输入流封装）
 */
public class Request {

    // 请求方式
    private String method;
    // 地址
    private String url;
    // 获取的输入流
    private InputStream inputStream;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public Request(InputStream inputStream) throws IOException {

        this.inputStream = inputStream;

        // 从输入流获取请求信息
        int count = 0;
        while (count == 0){
            count = inputStream.available();
        }

        byte[] bytes = new byte[count];
        inputStream.read(bytes);
        String inputStr = new String(bytes);
        System.out.println(inputStr);

        String firstLineStr = inputStr.split("\\n")[0];
        String[] split = firstLineStr.split(" ");
        System.out.println(split);

        this.method = split[0];
        this.url = split[1];
    }
}
