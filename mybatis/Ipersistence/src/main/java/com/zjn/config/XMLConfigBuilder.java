package com.zjn.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zjn.io.Resources;
import com.zjn.pojo.Configuration;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
/**
 * @author zjn
 * @create 2020-05-23 09:14
 * @description 对配置文件进行解析
 */
public class XMLConfigBuilder {

    private Configuration configuration;

    public XMLConfigBuilder() {
        this.configuration = new Configuration();
    }

    public Configuration parseConfig(InputStream inputStream) throws DocumentException, PropertyVetoException {

        // 把字节流读取过来
        Document document = new SAXReader().read(inputStream);

        Element rootElement = document.getRootElement();

        List<Element> list = rootElement.selectNodes("//property");

        // 存放熟悉的文件
        Properties properties = new Properties();

        for (Element e: list ) {
            String name = e.attributeValue("name");
            String value = e.attributeValue("value");
            properties.setProperty(name,value);
        }

        // 往c3po 连接池 注入配置属性
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();

        comboPooledDataSource.setDriverClass(properties.getProperty("driverClass"));
        comboPooledDataSource.setJdbcUrl(properties.getProperty("jdbcUrl"));
        comboPooledDataSource.setUser(properties.getProperty("username"));
        comboPooledDataSource.setPassword(properties.getProperty("password"));

        configuration.setDataSource(comboPooledDataSource);

        List<Element> mapperList = rootElement.selectNodes("//mapper");

        for (Element e: mapperList ) {
            String mapperPath = e.attributeValue("resource");
            InputStream resourceAsSteam = Resources.getResourceAsSteam(mapperPath);
            XMLMapperBuilder xmlConfigBuilder = new XMLMapperBuilder(configuration);
            xmlConfigBuilder.parse(resourceAsSteam);
        }

        return  configuration;

    }
}
