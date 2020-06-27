package server;

import java.io.IOException;

/**
 * @author: 190coder <190coder.cn>
 * @description: Servlet
 * @create: 2020-06-21 10:01
 */
public class ZjnServlet extends HttpServlet {
    @Override
    public void doGet(Request request, Response response) {
        String content = "<h1>ZjnServlet demo3 get</h1>";

        try {
            response.output(HttpProtocolUtil.getHttpHeader200(content.getBytes().length) + content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(Request request, Response response) {

        String content = "<h1>LagouServlet demo3 post</h1>";

        try {
            response.output(HttpProtocolUtil.getHttpHeader200(content.getBytes().length) + content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
