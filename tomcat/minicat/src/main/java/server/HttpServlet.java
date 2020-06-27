package server;

/**
 * @author: 190coder <190coder.cn>
 * @description: HttpServlet
 * @create: 2020-06-21 09:52
 */
public abstract class HttpServlet implements Servlet{

    public abstract void doGet(Request request,Response response);

    public abstract void doPost(Request request,Response response);

    @Override
    public void service(Request request, Response response) {
        if("GET".equalsIgnoreCase(request.getMethod())){
            doGet(request,response);
        }else {
            doPost(request,response);
        }
    }
}
