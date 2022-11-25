package com.limeare.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.limeare.reggie.common.BaseContext;
import com.limeare.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
//路径匹配器
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();
//        不处理路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        boolean check = check(requestURI, urls);
        if (check){
            filterChain.doFilter(request,response);
            return;
        }
        //判断登录状态，如果已登录，则直接放行
        //后台员工账号
        if(request.getSession().getAttribute("employee")!=null){

            Long empId= (Long) request.getSession().getAttribute("employee");

            //传入用户id
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }

        //前台用户账号
        if(request.getSession().getAttribute("user")!=null){

            Long userId= (Long) request.getSession().getAttribute("user");

            //传入用户id
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }

        response.getWriter().write(JSON.toJSONString(R.error("未登录")));
        return;
    }

//    路径匹配判断是否需要拦截
    public boolean check(String requestURI,String[] urls){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
