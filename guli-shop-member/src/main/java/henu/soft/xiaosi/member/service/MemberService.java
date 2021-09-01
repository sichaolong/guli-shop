package henu.soft.xiaosi.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import henu.soft.common.to.SocialUserTo;
import henu.soft.common.to.UserLoginTo;
import henu.soft.common.utils.PageUtils;
import henu.soft.common.to.MemberRegisterTo;
import henu.soft.xiaosi.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 23:33:19
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);


    /**
     * 用户注册
     * @param registerVo
     */
    void register(MemberRegisterTo registerVo);

    /**
     * 用户登录
     * @param loginVo
     * @return
     */

    MemberEntity login(UserLoginTo loginVo);

    /**
     * 社交账号登录
     * @param socialUserTo
     * @return
     */
    MemberEntity oauthLogin(SocialUserTo socialUserTo);


}

