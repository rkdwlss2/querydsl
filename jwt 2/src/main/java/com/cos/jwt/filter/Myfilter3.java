package com.cos.jwt.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class Myfilter3 implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

//        req.setCharacterEncoding("UTF-8");
        // 토큰 : 코스이 넘어 오면 인증 되도록 만들기
//        if (req.getMethod().equals("POST")){
//            System.out.println("post 요청됨");
//            String headerAuth = req.getHeader("Authorization");
//            System.out.println(headerAuth);
//            System.out.println("filter3");
//
//            if (headerAuth.equals("cos")){
//                chain.doFilter(req,res);
//            }else{
//                PrintWriter outPrintWriter = res.getWriter();
//                outPrintWriter.println("인증안됨");
//            }
//        }
    }
}
