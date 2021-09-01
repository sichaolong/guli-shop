package henu.soft.xiaosi.authserver.feign;


import henu.soft.common.to.MemberRegisterTo;
import henu.soft.common.to.SocialUserTo;
import henu.soft.common.utils.R;
import henu.soft.common.to.UserLoginTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@FeignClient("guli-shop-member")
public interface MemberFeignService {

    @RequestMapping("/member/member/register")
    R register(@RequestBody MemberRegisterTo registerVo);

    @RequestMapping("/member/member/login")
    R login(@RequestBody @Valid UserLoginTo vo);

    @RequestMapping("/member/member/oauthlogin")
    R oauthLogin(@RequestBody SocialUserTo socialUserTo);

}
