package server;

/**
 * @author: 190coder <190coder.cn>
 * @description: Servlet 接口
 * @create: 2020-06-21 09:51
 */
public interface Servlet {

    void service(Request request, Response response);
}
