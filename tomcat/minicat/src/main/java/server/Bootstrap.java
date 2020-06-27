package server;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import javax.transaction.xa.XAResource;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author: 190coder <190coder.cn>
 * @description: 启动类
 * @create: 2020-06-20 17:52
 */
public class Bootstrap {

    private int port = 8080;

    private Mapper mapper = new Mapper();

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    private Map<String,HttpServlet> servletMap = new HashMap<String,HttpServlet>();

    public static void main(String[] args) {

        Bootstrap bootstrap = new Bootstrap();
        try {
            // 启动Minicat
            bootstrap.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {

        loadServer();
        // 加载相关配置信息 xml
//        loadServlet();
        // 定义线程池
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10,100,
                100L,TimeUnit.SECONDS,new ArrayBlockingQueue<>(50), Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());

        ServerSocket serverSocket = new ServerSocket(port);

        System.out.println("=====>>>Minicat start on port：" + port);
        /*
            完成Minicat 1.0版本
            需求：浏览器请求http://localhost:8080,返回一个固定的字符串到页面"Hello Minicat!"
//         */
//        while (true){
//            Socket accept = serverSocket.accept();
//            System.out.println("sss");
//            // 获取输出流 写入
//            OutputStream outputStream = accept.getOutputStream();
//            String data = "Hello Minicat!  " + i;
//            String responseText = HttpProtocolUtil.getHttpHeader200(data.getBytes().length) + data;
//            outputStream.write(responseText.getBytes());
//            accept.close();
//        }
        /**
         * 完成Minicat 2.0版本
         * 需求：封装Request和Response对象，返回html静态资源文件
         */
//        while (true){
//            Socket socket = serverSocket.accept();
//            InputStream inputStream = socket.getInputStream();
//            // 封装Request对象和Response对象
//            Request request = new Request(inputStream);
//            Response response = new Response(socket.getOutputStream());
//
//            // 访问路径写入到 response
//            response.outputHtml(request.getUrl());
//            socket.close();
//
//        }

        /**
         * 完成Minicat 3.0版本
         * 需求：可以请求动态资源（Servlet）
         */
//        while (true){
//
//            Socket socket = serverSocket.accept();
//            InputStream inputStream = socket.getInputStream();
//            Request request = new Request(inputStream);
//            System.out.println(servletMap.get(request.getUrl())+ "22222");
//            Response response = new Response(socket.getOutputStream());
//
//            // 访问静态资源
//            if(servletMap.get(request.getUrl()) == null){
//                response.outputHtml(request.getUrl());
//            }else {
//                HttpServlet httpServlet = servletMap.get(request.getUrl());
//                httpServlet.service(request,response);
//            }
//
//            socket.close();
//
//        }

        System.out.println("=========>>>>>>使用线程池进行多线程改造");
        /*
            多线程改造（使用线程池）
         */
        while(true) {

            Socket socket = serverSocket.accept();
            RequestProcessor requestProcessor = new RequestProcessor(mapper,socket);
            //requestProcessor.start();
            threadPoolExecutor.execute(requestProcessor);
        }



    }

    /**
     * 加载web.xml 按照 web.xml 固定简单格式
     */
    private void loadServer(){

        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("server.xml");

        SAXReader saxReader = new SAXReader();

        try {
            Document document = saxReader.read(resourceAsStream);

            Element rootElement = document.getRootElement();
            // 获取到端口号
            Element connector = (Element) rootElement.selectSingleNode("//Server/Service/Connector");

            this.port = Integer.valueOf(connector.attributeValue("port"));
            // 获得Host
            Element hostElement = (Element) rootElement.selectSingleNode("//Server/Service/Engine/Host");
            String name = hostElement.attributeValue("name");
            String appBase = hostElement.attributeValue("appBase");


            // 初始化 host
            Mapper.Host host = new Mapper.Host();
            host.setName(name);
            host.setAppBase(appBase);
            // 找到 指定目录遍历项目 存入 context
            File file = new File(appBase);
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                // 过滤MAC 配置文件
                if(files[i].getName().contains(".DS_Store"))continue;

                Mapper.Context context = new Mapper.Context();

                System.out.println(files[i].getName());
                // 获取项目路径存入到context 例如 ../../demo1
                context.setContextName(files[i].getName());
                // 获取web.xml 准备加载
                File webXmlFile = new File(files[i].getPath()  + "/web.xml");
                // 存在xml 生成 流文件 加载 Servlet
                if(webXmlFile.exists()){
                    InputStream webXmlAsStream = new FileInputStream(webXmlFile);

                    loadServlet(webXmlAsStream,context,host,files[i].getName());
                }

            }


        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
    /**
     * 从xml 加载 url 对应的 servlet 类 反射实例化 存放到 Map 集合
     */
    private void loadServlet(InputStream inputStream, Mapper.Context context, Mapper.Host host, String projectName) {

//        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("web.xml");
        SAXReader saxReader = new SAXReader();
        // 实例化 Wrapper map 容器
        Mapper.Wrapper wrapper = new Mapper.Wrapper();

        try {
            Document document = saxReader.read(inputStream);

            Element rootElement = document.getRootElement();

            List<Element> selectNodes = rootElement.selectNodes("//servlet");

            for (int i = 0; i <selectNodes.size() ; i++) {

                Element element = selectNodes.get(i);

                Node servletNameElement = element.selectSingleNode("//servlet-name");

                String servletName = servletNameElement.getStringValue();

                Node servletClassElement = element.selectSingleNode("//servlet-class");

                String servletClass = servletClassElement.getStringValue();

                // 根据 servletName 找到 servlet-mapping -> url-pattern
                Element servletMapping = (Element) rootElement.selectSingleNode("/web-app/servlet-mapping[servlet-name = '" + servletName + "']");

                String urlPattern = servletMapping.selectSingleNode("//url-pattern").getStringValue();

                // 放入到Map
//                HttpServlet httpServlet = (HttpServlet)Class.forName(servletClass).newInstance();
//                servletMap.put(urlPattern,httpServlet);
                String realUrl = projectName + urlPattern;
//                String realServletClass = projectName  + "/server/ZjnServlet.class";

                // 加载指定路径下servlet path 放入 自定义加载类，反射创建servlet 使用
                MyClassLoader loader = new MyClassLoader();

                loader.setLocation(host.getAppBase() +"/" + projectName + "/");

//                System.out.println(servletClass);
//                System.out.println(loader.findClass(servletClass));
//                System.out.println(222);
                HttpServlet httpServlet = (HttpServlet)loader.findClass(servletClass).newInstance();
                // 把 项目名 + URL 匹配的 servlet 放入到 wrapper
                wrapper.setWrapperMap(realUrl,httpServlet);
                // 把 wrapper 加入到 context 内部为 add list
                context.setWrapperList(wrapper);
                // 把 context 加入到 host
                host.setContextList(context);
                // host 放入 mapper
                mapper.setHost(host);
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
}
