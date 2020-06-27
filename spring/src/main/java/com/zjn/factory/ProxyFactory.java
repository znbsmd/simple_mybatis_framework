package com.zjn.factory;

import com.zjn.utils.TransactionManager;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author zjn
 * @create 2020-05-28 21:48
 * @description
 */
public class ProxyFactory {

    private  TransactionManager transactionManager;

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public  Object getJdkProxy(Object obj){

        return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object result = null;

                try {

                    transactionManager.beginTransaction();

                    result = method.invoke(obj, args);

                    transactionManager.commit();
                }catch (Exception e){

                    transactionManager.rollback();

                }

                return result;
            }
        });
    }

    /**
     * 使用cglib动态代理
     * @param obj
     * @return
     */
    public Object getCglibProxy(Object obj) {
        return  Enhancer.create(obj.getClass(), new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                Object result = null;
                try{
                    // 开启事务
                    transactionManager.beginTransaction();

                    result = method.invoke(obj,objects);

                    // 提交事务
                    transactionManager.commit();
                }catch (Exception e) {
                    e.printStackTrace();
                    // 回滚事务
                    transactionManager.rollback();

                    // 抛出异常便于上层servlet捕获
                    throw e;

                }
                return result;
            }
        });
    }

}
