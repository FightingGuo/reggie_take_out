package com.ghc.reggie.filter;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/7/24 - 16:36
 */

import com.alibaba.fastjson.JSON;
import com.ghc.reggie.utils.BaseContext;
import com.ghc.reggie.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * 检查用户是否已经完成登录
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request= (HttpServletRequest)  servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;


        //1、获取本次请求的URI
        //定义不需要处理的请求路径
        String requestURI = request.getRequestURI();

//        log.info("拦截到请求:{}",requestURI);

        String[] urls=new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg", //移动端发送短信
                "/user/login"    //移动端登录
        };

        //2、判断本次请求是否需要处理
        boolean check = checkFilter(urls, requestURI);

        //3、如果不需要处理，则直接放行
        if(check){ //如果check为true说明requestURI在可放行的url地址内 直接放行
//            log.info("放行:{}",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //4-1、判断员工登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("employee")!=null){
             Long empId=(Long) request.getSession().getAttribute("employee");

            //把用户id存入ThreadLocal里
            BaseContext.setCurrentId(empId);

//            log.info("用户已登录，用户id为:{}",request.getSession().getAttribute("employee"));
            filterChain.doFilter(request,response);
            return;
        }

        //4-2、判断用户登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("user")!=null){
            Long userId=(Long) request.getSession().getAttribute("user");

            //把用户id存入ThreadLocal里
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }

        //5、如果未登录则返回未登录结果，通过输出流的方式去响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean checkFilter(String[] urls,String requestURI) {
        for (String url:urls) {
            boolean rest = PATH_MATCHER.match(url, requestURI);
            if (rest){
                return true;
            }
        }
        return false;
    }

}
