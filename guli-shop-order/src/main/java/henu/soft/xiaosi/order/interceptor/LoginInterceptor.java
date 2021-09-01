package henu.soft.xiaosi.order.interceptor;


import henu.soft.common.constant.AuthServerConstant;
import henu.soft.common.to.MemberResponseTo;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 登录拦截器，未登录的用户不能进入订单服务
 */

public class LoginInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberResponseTo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /**
         * 本微服务模块内请求调用直接放行
         */

        String requestURI = request.getRequestURI();
        AntPathMatcher matcher = new AntPathMatcher();
        boolean match1 = matcher.match("/order/order/infoByOrderSn/**", requestURI);
        boolean match2 = matcher.match("/payed/notify", requestURI);
        if (match1 || match2) return true;


        HttpSession session = request.getSession();
        MemberResponseTo memberResponseVo = (MemberResponseTo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        //登陆了
        if (memberResponseVo != null) {
            loginUser.set(memberResponseVo);
            return true;
        } else {
            session.setAttribute("msg", "请先登录!");
            response.sendRedirect("http://auth.gulishop.cn/login.html");
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
