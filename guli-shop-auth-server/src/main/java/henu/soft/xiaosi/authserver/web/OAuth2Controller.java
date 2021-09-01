package henu.soft.xiaosi.authserver.web;


import com.alibaba.cloud.commons.lang.StringUtils;
import henu.soft.common.constant.AuthServerConstant;
import henu.soft.common.to.MemberResponseTo;

import henu.soft.common.utils.HttpUtil;
import henu.soft.common.utils.R;
import henu.soft.xiaosi.authserver.feign.MemberFeignService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;


import henu.soft.common.to.SocialUserTo;
import jdk.jfr.ContentType;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理社交登录
 */

@Controller
public class OAuth2Controller {


    @Autowired
    private MemberFeignService memberFeignService;
    //http://auth.gulishop.cn/oauth2.0/gitee/success
    @RequestMapping("/oauth2.0/gitee/success")
    public String authorize(@RequestParam("code") String code, HttpSession session) throws Exception {
        //1. 使用code换取token，换取成功则继续2，否则重定向至登录页
        // http://gulishop.cn/oauth2.0/gitee/success?code=5cf1dab80d4b62fca4886c05fc298fe01c16efc5b663a057aa256c1bf5389e96
        Map<String, String> query = new HashMap<>();
        query.put("client_id", "40bfef56bc4f1ba0749ac79a4186ff3b9f2c08915ffa7e473f66a60b6f194886");
        query.put("client_secret", "a3fd669d30272b2e051e12017297189cd4a3944cb48227c8c466f967a302c93b");
        query.put("grant_type", "authorization_code");
        query.put("redirect_uri", "http://auth.gulishop.cn/oauth2.0/gitee/success");
        query.put("code", code);

        //发送post请求换取token
        // https://gitee.com/oauth/token?grant_type=authorization_code&code={code}&client_id={client_id}&redirect_uri={redirect_uri}&client_secret={client_secret}
        HttpResponse response = HttpUtil.doPost("https://gitee.com", "/oauth/token", "post", new HashMap<String, String>(), query, new HashMap<String, String>());
        Map<String, String> errors = new HashMap<>();
        if (response.getStatusLine().getStatusCode() == 200) {
            //2. 调用member远程接口进行oauth登录，登录成功则转发至首页并携带返回用户信息，否则转发至登录页
            String json = EntityUtils.toString(response.getEntity());
            SocialUserTo socialUserTo = JSON.parseObject(json, new TypeReference<SocialUserTo>() {
            });
            // 拿着accessToken查询用户信息
            if (socialUserTo != null && (!StringUtils.isEmpty(socialUserTo.getAccess_token()))) {

                Map<String, String> queryAccessToken = new HashMap<>();
                queryAccessToken.put("access_token", socialUserTo.getAccess_token());

                Map<String, String> queryHeaders = new HashMap<>();
                queryHeaders.put("Content-Type", "application/json;charset=UTF-8");

                HttpResponse response1 = HttpUtil.doGet("https://gitee.com", "/api/v5/user", "get", queryHeaders, queryAccessToken);
                if (response1.getStatusLine().getStatusCode() == 200) {
                    String json1 = EntityUtils.toString(response1.getEntity());

                    // 获取user_info的id
                    SocialUserTo socialUserTo1 = JSON.parseObject(json1, new TypeReference<SocialUserTo>() {
                    });
                    socialUserTo1.setAccess_token(socialUserTo.getAccess_token());
                    socialUserTo1.setExpires_in(socialUserTo.getExpires_in());

                    // TODO 社交账号登录和注册为一体
                    R login = memberFeignService.oauthLogin(socialUserTo1);
                    //2.1 远程调用成功，返回首页并携带用户信息
                    if (login.getCode() == 0) {
                        String jsonString = JSON.toJSONString(login.get("memberEntity"));
                        System.out.println("----------------" + jsonString);
                        MemberResponseTo memberResponseTo = JSON.parseObject(jsonString, new TypeReference<MemberResponseTo>() {
                        });
                        System.out.println("----------------" + memberResponseTo);


                        // TODO 分布式session,存入redis中，但是默认存放的是auth域名下，父域product服务不能使用，而且序列化方式是jdk序列化
                        // TODO 1. session域的提升
                        // TODO 2. 改为json序列化



                        session.setAttribute(AuthServerConstant.LOGIN_USER, memberResponseTo);
                        return "redirect:http://gulishop.cn";
                    } else {
                        //2.2 否则返回登录页
                        errors.put("msg", "登录失败，请重试");
                        session.setAttribute("errors", errors);
                        return "redirect:http://auth.gulishop.cn/login.html";
                    }
                } else {
                    errors.put("msg", "获得第三方授权失败，请重试");
                    session.setAttribute("errors", errors);
                    return "redirect:http://auth.gulishop.cn/login.html";
                }
            }

        }
        errors.put("msg", "获得第三方授权失败，请重试");
        session.setAttribute("errors", errors);
        return "redirect:http://auth.gulishop.cn/login.html";


    }
}
