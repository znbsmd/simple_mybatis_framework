package com.zjn.factory;

import com.zjn.annotation.Autowired;
import com.zjn.annotation.Service;
import com.zjn.annotation.Transactional;
import com.zjn.utils.ClassUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zjn
 * @create 2020-05-28 16:56
 * @description
 */
public class BeanFactory {
    public BeanFactory() {
    }

    private static Map<String,Object> map = new HashMap<>();  // 存储对象

    static {

        try {
            // 解析 @Service 注解
            parseServiceAnnotation();
            // 解析 xml
            parseXml();
            // 从 map 处理 @Autowired 并注入 属性
            setAutowiredAnnotation();
            // 处理 @Transactional
            parseTransactionalAnnotation();

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }


    // 对外提供获取实例对象的接口（根据id获取）
    public static  Object getBean(String id) {
        return map.get(id);
    }

    /**
     * 解析 @Service 注解
     *
     * @throws Exception
     */
    public static void parseServiceAnnotation() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        // 获取包下类
        Set<Class<?>> classes = ClassUtils.getClasses("com.zjn");
        // 先获取 service注解 并放入 map
        for (Class singleClass : classes) {

            String id;
            // 找到Service在做处理
            if(singleClass.getAnnotation(Service.class) != null){

                Service serviceAn = (Service) singleClass.getAnnotation(Service.class);

                String serviceValue = serviceAn.value();
                // 判断注解有无value属性值
                if(serviceValue.equals("")){
                    // 获取实现的接口名 作为 map key
                    String simpleName = singleClass.getInterfaces()[0].getSimpleName();
                    // 首字母小写
                    id = toLowerFirstCase(simpleName);

                }else{

                    id = serviceValue;
                }
                Object o = singleClass.getDeclaredConstructor().newInstance();

                map.put(id,o);

            }

        }

    }

    /**
     * 从 map 处理 @Autowired 并注入 属性
     *
     * @throws IllegalAccessException
     */
    private static void setAutowiredAnnotation() throws IllegalAccessException {

        // 为空 抛出异常
        if(map.isEmpty()){
            throw new NullPointerException();
        }
        // 遍历存放属性的map
        for (Object value : map.values()) {
            // 获取属性
            Field[] fields = value.getClass().getDeclaredFields();
            for (Field field:fields ) {
                // 暴力访问
                field.setAccessible(true);
                // 如果注解存在，设置属性值
                if(field.getAnnotation(Autowired.class) != null){
                    // 获取已存在的接口id
                    String simpleName = field.getType().getSimpleName();
                    // 首字母小写
                    String id = toLowerFirstCase(simpleName);

                    Object o = map.get(id);

                    if (o != null) {
                        field.set(value, o);
                    }
                }
            }
        }
    }

    /**
     * 首字母小写 取key 用
     *
     * @param name
     * @return
     */
    private static String toLowerFirstCase(String name){

        return  name.substring(0,1).toLowerCase()+name.substring(1);
    }
    /**
     * 处理 @Transactional
     */
    private static void parseTransactionalAnnotation(){

        // 为空 抛出异常
        if(map.isEmpty()){
            throw new NullPointerException();
        }
        // 获取xml 配置的动态代理工厂
        ProxyFactory proxyFactory = (ProxyFactory)getBean("proxyFactory");
        // 遍历存放属性的map
        for (String key : map.keySet()) {

            Object o; Object value = map.get(key);
            // 如果Transactional注解存在 做判断处理
            if(value.getClass().getAnnotation(Transactional.class) != null){

                // 如果有实现接口 使用jdk代理
                if(value.getClass().isInterface()){
                    o = proxyFactory.getJdkProxy(value);
                }else{
                    o = proxyFactory.getCglibProxy(value);
                }

                map.put(key, o);

            }
        }
    }

    /**
     * 解析xml配置文件
     *
     * @throws DocumentException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    private static void parseXml() throws DocumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        // 加载xml 通过反射实例化存储到map
        InputStream resourceAsStream = BeanFactory.class.getClassLoader().getResourceAsStream("beans.xml");
        // 解析xml
        SAXReader saxReader = new SAXReader();

        Document document = saxReader.read(resourceAsStream);
        Element rootElement = document.getRootElement();
        // 获取Bean
        List<Element> beanList = rootElement.selectNodes("//bean");
        for (int i = 0; i < beanList.size(); i++) {
            Element element =  beanList.get(i);
            // 处理每个bean元素，获取到该元素的id 和 class 属性
            String id = element.attributeValue("id");        // 接口名作为 id
            String clazz = element.attributeValue("class");  // 实现类
            // 通过反射技术实例化对象
            Class<?> aClass = Class.forName(clazz);
            Object o = aClass.getDeclaredConstructor().newInstance();  // 实例化
            // 存储到map中待用
            map.put(id,o);

        }

        // 获取 解析property 依赖注入
        List<Element> propertyList = rootElement.selectNodes("//property");
        // 解析property，获取父元素
        for (int i = 0; i < propertyList.size(); i++) {
            Element element =  propertyList.get(i);   //<property name="AccountDao" ref="accountDao"></property>
            String name = element.attributeValue("name");
            String ref = element.attributeValue("ref");

            // 找到当前需要被处理依赖关系的bean
            Element parent = element.getParent();

            // 调用父元素对象的反射功能
            String parentId = parent.attributeValue("id");
            Object parentObject = map.get(parentId);
            // 遍历父对象中的所有方法，找到"set" + name
            Method[] methods = parentObject.getClass().getMethods();
            for (int j = 0; j < methods.length; j++) {
                Method method = methods[j];
                if(method.getName().equalsIgnoreCase("set" + name)) {  // 该方法就是 setAccountDao(AccountDao accountDao)
                    method.invoke(parentObject,map.get(ref));
                }
            }
            // 把处理之后的parentObject重新放到map中
            map.put(parentId,parentObject);

        }
    }
}
