package henu.soft.xiaosi.authserver.web;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import henu.soft.common.constant.AuthServerConstant;
import henu.soft.common.exception.BizCodeEnume;
import henu.soft.common.to.MemberRegisterTo;
import henu.soft.common.to.MemberResponseTo;
import henu.soft.common.utils.R;
import henu.soft.xiaosi.authserver.feign.MemberFeignService;
import henu.soft.xiaosi.authserver.feign.ThirdPartFeignService;
import henu.soft.common.to.UserLoginTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class LoginController {

    @Autowired
    ThirdPartFeignService thirdPartFeignService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    MemberFeignService memberFeignService;


    @GetMapping("/sms/sendcode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone) {

        //1、接口防刷
        String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (!StringUtils.isEmpty(redisCode)) {
            //活动存入redis的时间，用当前时间减去存入redis的时间，判断用户手机号是否在60s内发送验证码
            long currentTime = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - currentTime < 60000) {
                //60s内不能再发
                return R.error(BizCodeEnume.SMS_CODE_EXCEPTION.getCode(), BizCodeEnume.SMS_CODE_EXCEPTION.getMsg());
            }
        }

        //2、验证码的再次效验 redis.存key-phone,value-code
        int code = (int) ((Math.random() * 9 + 1) * 100000);
        String codeNum = String.valueOf(code);
        String redisStorage = codeNum + "_" + System.currentTimeMillis();

        //存入redis，防止同一个手机号在60秒内再次发送验证码
        stringRedisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone,
                redisStorage, 10, TimeUnit.MINUTES);

        return thirdPartFeignService.sendCode(phone, codeNum);


    }

    /**
     * 注册
     */

    /**
     * TODO: 重定向携带数据：RedirectAttributes,利用session原理，将数据放在session中。
     * TODO:只要跳转到下一个页面取出这个数据以后，session里面的数据就会删掉
     * TODO：分布下session问题
     * RedirectAttributes：重定向也可以保留数据，不会丢失
     * 用户注册
     *
     * @return
     */
    @PostMapping("/register")
    public String register(@Valid MemberRegisterTo vos, BindingResult result, RedirectAttributes attributes) {
        //如果有错误回到注册页面
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

            // 模拟重定向携带数据
            attributes.addFlashAttribute("errors", errors);
            //效验出错回到注册页面
            return "redirect:http://auth.gulishop.cn/reg.html";
        }

        //1、效验验证码
        String code = vos.getCode();

        //获取存入Redis里的验证码
        String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vos.getPhone());
        if (!StringUtils.isEmpty(redisCode)) {
            //截取字符串
            if (code.equals(redisCode.split("_")[0])) {
                //删除验证码;令牌机制
                stringRedisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vos.getPhone());
                //验证码通过，真正注册，调用远程服务进行注册
                R register = memberFeignService.register(vos);
                if (register.getCode() == 0) {
                    //成功
                    return "redirect:http://auth.gulishop.cn/login.html";
                } else {
                    //失败
                    Map<String, String> errors = new HashMap<>();
                    TypeReference<String> typeReference = new TypeReference<String>() {
                    };
                    String msg = register.getData(typeReference);
                    errors.put("msg",msg );
                    attributes.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.gulishop.cn/reg.html";
                }
            } else {
                //效验出错回到注册页面
                Map<String, String> errors = new HashMap<>();
                errors.put("code", "验证码错误");
                attributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.gulishop.cn/reg.html";
            }
        } else {
            //效验出错回到注册页面
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码错误");
            attributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulishop.cn/reg.html";
        }
    }


    /**
     * 登录,查询用户信息
     */
    @RequestMapping("/login")
    public String login(UserLoginTo vo, RedirectAttributes attributes, HttpSession session){
        R r = memberFeignService.login(vo);
        if (r.getCode() == 0) {
            String jsonString = JSON.toJSONString(r.get("memberEntity"));
            MemberResponseTo memberResponseTo = JSON.parseObject(jsonString, new TypeReference<MemberResponseTo>() {
            });

            session.setAttribute(AuthServerConstant.LOGIN_USER,memberResponseTo);

            return "redirect:http://gulishop.cn/";
        }else {
            String msg = (String) r.get("msg");
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", msg);
            attributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulishop.cn/login.html";
        }
    }

}
