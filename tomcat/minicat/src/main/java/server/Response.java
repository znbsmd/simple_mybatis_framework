package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 封装Response对象，需要依赖于OutputStream
 *
 * 该对象需要提供核心方法，输出html
 */
public class Response {


    private OutputStream outputStream;

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public Response() {
    }

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    // 把内容输出到浏览器
    public void output(String content) throws IOException {
        this.outputStream.write(content.getBytes());
    }

    // 把本地资源路径处理成输出流 输出
    public void outputHtml(String path) throws IOException {

        String absolutePath = StaticResourceUtil.getAbsolutePath(path);

        File file = new File(absolutePath);

        if(file.exists() && file.isFile()){

            StaticResourceUtil.outputStaticResource(new FileInputStream(file),outputStream);

        }else{

            // 输出404
            output(HttpProtocolUtil.getHttpHeader404());
        }
    }
}
