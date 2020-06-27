package com.zjn.servlet;

import com.zjn.annotation.Autowired;
import com.zjn.dao.AccountDao;
import com.zjn.factory.BeanFactory;
import com.zjn.factory.ProxyFactory;
import com.zjn.pojo.Result;
import com.zjn.service.TransferService;
import com.zjn.service.impl.TransferServiceImpl;
import com.zjn.utils.JsonUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zjn
 * @create 2020-05-28 15:28
 * @description
 */

@WebServlet(name="transferServlet",urlPatterns = "/transferServlet")
public class TransServlet  extends HttpServlet {

    @Override
    public void init(){
        BeanFactory b = new BeanFactory();

    }

    //    private ProxyFactory proxyFactory = (ProxyFactory) BeanFactory.getBean("proxyFactory");
    //    private TransferService transferService = (TransferService) proxyFactory.getJdkProxy(BeanFactory.getBean("transferService")) ;
    private TransferService transferService = (TransferService) BeanFactory.getBean("one");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 设置请求体的字符编码
        req.setCharacterEncoding("UTF-8");

        String fromCardNo = req.getParameter("fromCardNo");
        String toCardNo = req.getParameter("toCardNo");
        String moneyStr = req.getParameter("money");
        int money = Integer.parseInt(moneyStr);

        Result result = new Result();
        try {

            // 2. 调用service层方法
            boolean transfer = transferService.transfer(fromCardNo, toCardNo, money);
            if(transfer){

                result.setStatus("200");
            }else {
                result.setStatus("201");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setStatus("201");
            result.setMessage(e.toString());
        }

        // 响应
        resp.setContentType("application/json;charset=utf-8");
        resp.getWriter().print(JsonUtils.object2Json(result));
    }
}
