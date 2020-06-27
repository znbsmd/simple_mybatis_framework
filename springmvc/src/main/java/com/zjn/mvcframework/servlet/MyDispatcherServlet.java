package com.zjn.mvcframework.servlet;

import com.zjn.mvcframework.annotations.*;
import com.zjn.mvcframework.pojo.Handler;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: 190coder <190coder.cn>
 * @description:
 * @create: 2020-06-07 12:21
 */
public class MyDispatcherServlet extends HttpServlet {

    // 定义一个Properties 存放配置文件
    private Properties properties = new Properties();
    // 缓存扫描到的类的全限定类名
    private List<String> classNames = new ArrayList<>();

    private List<Handler>  handlerMapping = new ArrayList<>();
    // ioc 容器
    private HashMap<String,Object> ioc = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 处理请求 根据url 找到 method 进行反射调用
        Handler handler = getHandler(req);

        if(handler == null) {
            resp.getWriter().write("404 not found");
            return;
        }

        // 根据get username 请求参数 判断权限
        String userName = req.getParameter("username");
        if(!authByUserName(handler, userName)){
            resp.getWriter().write(userName +" is no auth !!!");
            return;
        }


        // 获取所有方法的参数类型
        Class<?>[] parameterTypes = handler.getMethod().getParameterTypes();

        // 创建填充实际请求参数的空数组
        Object[] paraValues = new Object[parameterTypes.length];

        // 遍历实际请求的参数 和handle 设置好的参数名和位置map容器进行匹配，
        // 构造出 可以满足代理invoke 第二个参数的 数组
        Map<String, String[]> parameterMap = req.getParameterMap();

        for (Map.Entry<String,String[]> param : parameterMap.entrySet()) {

            // servlet 把 name=1&name=2  封装成  name [1,2] 把结果 格式化成 1,2
            String value = StringUtils.join(param.getValue(), ",");

            if(!handler.getParamIndexMapping().containsKey(param.getKey())){ continue;}

            // 参数匹配上了 把参数值放入数组
            Integer integer = handler.getParamIndexMapping().get(param.getKey());
            paraValues[integer] =  value;

        }

        // request 默认参数
        int requestIndex = handler.getParamIndexMapping().get(HttpServletRequest.class.getSimpleName()); // 0
        paraValues[requestIndex] = req;

        // response 默认参数
        int responseIndex = handler.getParamIndexMapping().get(HttpServletResponse.class.getSimpleName()); // 1
        paraValues[responseIndex] = resp;

        // 最终步，代理执行请求对应的方法
        try {
//            System.out.println(handler.getController());
//            System.out.println(paraValues);
            handler.getMethod().invoke(handler.getController(),paraValues);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    /**
     * 处理请求中的url
     *
     * @param req
     * @return
     */
    private Handler getHandler(HttpServletRequest req) {
        if(handlerMapping.isEmpty()) return null;

        String uri = req.getRequestURI();
        for (Handler handler: handlerMapping) {
            Matcher matcher = handler.getPattern().matcher(uri);
            if(!matcher.matches()) continue;

            return handler;
        }

        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        // 加载web.xml  init-param 中定义的参数
        String contextConfigLocation = config.getInitParameter("contextConfigLocation");
        doLoadConfig(contextConfigLocation);
        // 扫描包
        doScan(properties.getProperty("scanPackage"));
        // Bean 初始化 
        doInstance();
        // 依赖注入属性
        doAutoWired();
        // HandlerMapping 处理映射器
        initHandlerMapping();
        // 处理请求

        System.out.println("mvc init success...");
    }

    /**
     * security 权限判定
     *
     * @param handler handler在init阶段已经封装完，根据请求获取到当前handle
     * @param username 请求参数 username
     *
     */
    private boolean authByUserName(Handler handler,String username){

        // 获取controller
        Class<?> aClass = handler.getController().getClass();
        // 获取Method
        Method method = handler.getMethod();

        // 判断method auth
        MySecurity methodSecurity = method.getAnnotation(MySecurity.class);
        if(methodSecurity != null){
            // 下标判断是否存在数组中
            if(Arrays.binarySearch(methodSecurity.value(), username) >= 0) return true;

        }
        // 判断controller auth
        MySecurity controllerSecurity = aClass.getAnnotation(MySecurity.class);
        if(controllerSecurity != null){
            if(Arrays.binarySearch(controllerSecurity.value(), username) >= 0) return true;

        }

        return false;

    }
    /**
     *
     * 设置 url 和方法 映射关系  存入list中，
     */
    private void initHandlerMapping() {
        if(ioc.isEmpty()) return;

        for (Map.Entry<String,Object> entry: ioc.entrySet() ) {

            Class<?> aClass = entry.getValue().getClass();

            if(!aClass.isAnnotationPresent(MyController.class)) continue;

            String baseUrl = "";
            // 获取自定义url value
            if(aClass.isAnnotationPresent(MyRequestMapping.class)){

                MyRequestMapping annotation = aClass.getAnnotation(MyRequestMapping.class);

                baseUrl = annotation.value();

            }
            // 获取方法
            Method[] methods = aClass.getMethods();

            for (Method method: methods) {
                //  方法没有标识，就不处理
                if(!method.isAnnotationPresent(MyRequestMapping.class)) continue;

                MyRequestMapping annotation = method.getAnnotation(MyRequestMapping.class);

                String methodUrl = annotation.value();

                String url = baseUrl + methodUrl;    // 类 value + method value 为 请求url

                System.out.println(Pattern.compile(url));
                // 把数据以对象的形式封装成 handle 类 为了在请求中 反射调用 对应方法 因为 invoke 参数需要 object
                Handler handler = new Handler(entry.getValue(),method, Pattern.compile(url));

                // 处理参数
                Parameter[] parameters = method.getParameters();

                for (int j = 0; j < parameters.length; j++) {
                    Parameter parameter = parameters[j];
//                    System.out.println(parameter.getType().getSimpleName());
//                    System.out.println(j);
                    if(parameter.getType() == HttpServletRequest.class || parameter.getType() == HttpServletResponse.class){
                        handler.getParamIndexMapping().put(parameter.getType().getSimpleName(),j);
//                        System.out.println(handler.getParamIndexMapping().get(parameter.getType().getSimpleName()));
                    }else{
                        handler.getParamIndexMapping().put(parameter.getName(),j);
                    }
                }

                // 设置url method 映射关系
                handlerMapping.add(handler);
            }


        }
    }

    /**
     * 注入属性
     */
    private void doAutoWired() {

        if(ioc.isEmpty()) return;

        for (Map.Entry<String,Object> entry : ioc.entrySet()) {
            Field[] declaredFields = entry.getValue().getClass().getDeclaredFields();

            for (Field f: declaredFields) {
                if(!f.isAnnotationPresent(MyAutoWired.class)) {
                    continue;
                }

                MyAutoWired annotation = f.getAnnotation(MyAutoWired.class);
                String beanName = annotation.value();
                // 无自定义value
                if("".equals(beanName.trim())){
                     beanName = f.getType().getName();

                }
                f.setAccessible(true);
                try {
                    f.set(entry.getValue(),ioc.get(beanName));
                }catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 反射实例话对象 放入ioc 容器中
     */
    private void doInstance() {
        if(classNames.size() == 0) return;

        try {

            for (int i = 0; i < classNames.size(); i++) {
                String className = classNames.get(i);

                Class<?> aClass = Class.forName(className);

                if(aClass.isAnnotationPresent(MyController.class)){

                    String simpleName = aClass.getSimpleName();
                    String lowerFirstSimpleName = lowerFirst(simpleName);
                    Object o = aClass.newInstance();
                    ioc.put(lowerFirstSimpleName,o);

                }else if(aClass.isAnnotationPresent(MyService.class)){

                    Object o = aClass.newInstance();

                    MyService annotation = aClass.getAnnotation(MyService.class);

                    String beanName = annotation.value();
                    // 自定义以自定义value 为准
                    if(!"".equals(beanName)){
                        ioc.put(beanName,o);
                    }else{
                        // 如果没有指定，就以类名首字母小写
                        beanName = lowerFirst(aClass.getSimpleName());
                        ioc.put(beanName,o);
                    }

                    // service层往往是有接口的，面向接口开发，如果有接口 覆盖 value
                    Class<?>[] interfaces = aClass.getInterfaces();

                    for (int j = 0; j < interfaces.length; j++) {

                        Class<?> anInterface = interfaces[j];
                        ioc.put(lowerFirst(anInterface.getName()),o);
                    }


                }else {
                    continue;
                }
            }

        }catch (Exception e){

            e.printStackTrace();
        }
    }

    /**
     * 扫描 配置包下的所有类 加入list 集合
     *
     * @param scanPackage
     */
    private void doScan(String scanPackage) {

//        System.out.println(Thread.currentThread().getContextClassLoader().getResource("")+"2222");
//        System.out.println(Thread.currentThread().getContextClassLoader().getResource("/")+"3333");
//        System.out.println(MyDispatcherServlet.class.getResource("/")+"4444");
        String scanPackagePath = Thread.currentThread().getContextClassLoader().getResource("").getPath() + scanPackage.replaceAll("\\.","/");

        // 把扫包的需要加载的类转换file
        File pack = new File(scanPackagePath);

        File[] files = pack.listFiles();

        for (File file : files) {
            if(file.isDirectory()){
                doScan(scanPackage + "." + file.getName());
            }else if(file.getName().endsWith(".class")){
                String className = scanPackage + "." + file.getName().replaceAll(".class", "");

                classNames.add(className);
            }

        }

    }

    /**
     * 加载配置文件
     *
     * @param contextConfigLocation
     */
    private void doLoadConfig(String contextConfigLocation) {

        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);

        // 放入 properties 容器
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // 首字母小写方法
    private String lowerFirst(String str) {
        char[] chars = str.toCharArray();
        if('A' <= chars[0] && chars[0] <= 'Z') {
            chars[0] += 32;
        }
        return String.valueOf(chars);
    }

}
