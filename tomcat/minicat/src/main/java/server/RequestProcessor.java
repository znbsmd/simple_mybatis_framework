package server;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;
import java.util.Optional;

/**
 * @author: 190coder <190coder.cn>
 * @description: RequestProcessor run 任务
 * @create: 2020-06-21 11:33
 */
public class RequestProcessor implements Runnable{

    private Mapper mapper;
    private Socket socket;

    public RequestProcessor(Mapper mapper, Socket socket) {
        this.mapper = mapper;
        this.socket = socket;
    }

    @Override
    public void run() {
        try{
            InputStream inputStream = socket.getInputStream();

            // 封装Request对象和Response对象
            Request request = new Request(inputStream);
            Response response = new Response(socket.getOutputStream());

            String url = request.getUrl();
//            InetAddress inetAddress = socket.getInetAddress();
//            String hostName = inetAddress.getHostName();
//            if(hostName != mapper.getHost().getName()){
//
//                response.output(HttpProtocolUtil.getHttpHeader404());
//
//            }
            // 获取 demo2 路径 并匹配context
            String[] split = url.split("/");

            String contextUrl = split[1];

            // 从容器中找到 context
            Mapper.Context context = mapper.getHost().getContextList().stream().
                    filter(v -> v.getContextName().equals(contextUrl)).
                    findFirst().orElse(null);

            // 找不到项目 返回404
            if(context == null){

                response.output(HttpProtocolUtil.getHttpHeader404());

            }
            // 找 servlet
            for (Mapper.Wrapper wrapper: context.getWrapperList()) {

                HttpServlet httpServlet = wrapper.getWrapperByUrl(split[1] +"/"+split[2]);

                if(httpServlet == null){
                    continue;
                }

                // 动态资源servlet请求
                httpServlet.service(request,response);

            }

            // servlet啥都没有 -> 静态资源处理
            if(context.getWrapperList().isEmpty()) {
                response.outputHtml(request.getUrl());
            }

            socket.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
