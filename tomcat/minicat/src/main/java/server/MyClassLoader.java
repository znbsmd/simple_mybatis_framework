package server;

import java.io.*;

/**
 * @author: 190coder <190coder.cn>
 * @description: 自定义加载类
 * @create: 2020-06-23 15:57
 */
public class MyClassLoader extends ClassLoader{

    /**
     * name class 类的文件名
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] datas = loadClassData(name);
        return defineClass(name, datas, 0, datas.length);
    }

    // 指定文件目录
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    protected byte[] loadClassData(String name)
    {
        FileInputStream fis = null;
        byte[] datas = null;
        try
        {
            fis = new FileInputStream(location+name.replace(".","/")+".class");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int b;
            while( (b=fis.read())!=-1 )
            {
                bos.write(b);
            }
            datas = bos.toByteArray();
            bos.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(fis != null)
                try
                {
                    fis.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
        }
        return datas;

    }

}
