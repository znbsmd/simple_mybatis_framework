package com.zjn.demo.controller;

import com.zjn.demo.service.IDemoService;
import com.zjn.mvcframework.annotations.MyAutoWired;
import com.zjn.mvcframework.annotations.MyController;
import com.zjn.mvcframework.annotations.MyRequestMapping;
import com.zjn.mvcframework.annotations.MySecurity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: 190coder <190coder.cn>
 *
 * @description: demo
 *
 * @create: 2020-06-07 11:57
*/

@MyController
@MyRequestMapping("/demo")
@MySecurity(value = {"wc"} )
public class DemoController {

    @MyAutoWired
    private IDemoService demoService;

    @MyRequestMapping("/query")
    public  String query(HttpServletRequest request, HttpServletResponse response, String username) throws IOException {

        responseWriter(response,username);

        return  demoService.get(username);
    }

    @MyRequestMapping("/queryAuth1")
    @MySecurity(value = {"zjn","znbsmd"} )
    public  String queryAuth1(HttpServletRequest request, HttpServletResponse response, String username) throws IOException {

        responseWriter(response,username);

        return  demoService.get(username);
    }

    @MyRequestMapping("/queryAuth2")
    @MySecurity(value = {"huha","huha2"} )
    public  String queryAuth2(HttpServletRequest request, HttpServletResponse response, String username) throws IOException {

        responseWriter(response,username);

        return  demoService.get(username);
    }

    private void responseWriter(HttpServletResponse response, String username) throws IOException {

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("  🎉🎉🎉🎉  " + username + " 您终于进来了");
    }
}
