package henu.soft.xiaosi.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import henu.soft.common.exception.PhoneNumExistException;
import henu.soft.common.exception.UserExistException;
import henu.soft.common.to.MemberRegisterTo;
import henu.soft.common.to.SocialUserTo;
import henu.soft.common.to.UserLoginTo;
import henu.soft.common.utils.HttpUtil;
import henu.soft.xiaosi.member.entity.MemberLevelEntity;
import henu.soft.xiaosi.member.service.MemberLevelService;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import henu.soft.common.utils.PageUtils;
import henu.soft.common.utils.Query;

import henu.soft.xiaosi.member.dao.MemberDao;
import henu.soft.xiaosi.member.entity.MemberEntity;
import henu.soft.xiaosi.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }


    @Autowired
    MemberLevelService memberLevelService;


    @Override
    public void register(MemberRegisterTo registerVo) {
        //1 检查电话号是否唯一
        checkPhoneUnique(registerVo.getPhone());
        //2 检查用户名是否唯一
        checkUserNameUnique(registerVo.getUserName());
        //3 该用户信息唯一，进行插入
        MemberEntity entity = new MemberEntity();
        //3.1 保存基本信息
        entity.setUsername(registerVo.getUserName());
        entity.setMobile(registerVo.getPhone());
        entity.setCreateTime(new Date());
        //3.2 使用加密保存密码
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodePassword = passwordEncoder.encode(registerVo.getPassword());
        entity.setPassword(encodePassword);
        entity.setNickname(registerVo.getUserName());
        //3.3 设置会员默认等级
        //3.3.1 找到会员默认登记
        MemberLevelEntity defaultLevel = memberLevelService.getOne(new QueryWrapper<MemberLevelEntity>().eq("default_status", 1));
        //3.3.2 设置会员等级为默认
        entity.setLevelId(defaultLevel.getId());

        // 4 保存用户信息
        this.save(entity);
    }

    private void checkUserNameUnique(String userName) {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if (count > 0) {
            throw new UserExistException();
        }
    }

    private void checkPhoneUnique(String phone) {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (count > 0) {
            throw new PhoneNumExistException();
        }
    }


    /**
     * 登录
     *
     * @param loginVo
     * @return
     */
    @Override
    public MemberEntity login(UserLoginTo loginVo) {
        String loginAccount = loginVo.getLoginAccount();
        //以用户名或电话号登录的进行查询
        MemberEntity entity = this.getOne(new QueryWrapper<MemberEntity>().eq("username", loginAccount).or().eq("mobile", loginAccount));
        if (entity != null) {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            boolean matches = bCryptPasswordEncoder.matches(loginVo.getPassword(), entity.getPassword());
            if (matches) {
                entity.setPassword("");
                return entity;
            }
        }
        return null;
    }


    /**
     * 社交账号登陆、注册合并
     *auth-server传递的 SocialUserTo 包含
     * - id：第三方用户唯一标识
     * - token：令牌
     * - expires_in : 失效时间
     * - name : 用户昵称
     * - avatar_url: 用户头像
     * -
     * @param
     * @return
     */
    @Override
    public MemberEntity oauthLogin(SocialUserTo socialUserTo) {
        MemberEntity uid = this.getOne(new QueryWrapper<MemberEntity>().eq("social_uid", socialUserTo.getId()));
        //1 如果之前未登陆过，则查询其社交信息进行注册
        if (uid == null) {

            //调用微博api接口获取用户信息
            String json = null;
            try {

                Map<String, String> queryAccessToken = new HashMap<>();
                queryAccessToken.put("access_token", socialUserTo.getAccess_token());

                Map<String, String> queryHeaders = new HashMap<>();
                queryHeaders.put("'Content-Type", "application/json;charset=UTF-8");

                HttpResponse response1 = HttpUtil.doGet("https://gitee.com", "/api/v5/user", "get", queryHeaders, queryAccessToken);

                json = EntityUtils.toString(response1.getEntity());
            } catch (Exception e) {
                e.printStackTrace();
            }

            //封装用户信息并保存
            uid = new MemberEntity();
            uid.setAccessToken(socialUserTo.getAccess_token());
            uid.setSocialUid(socialUserTo.getId());
            uid.setExpiresIn(socialUserTo.getExpires_in());
            MemberLevelEntity defaultLevel = memberLevelService.getOne(new QueryWrapper<MemberLevelEntity>().eq("default_status", 1));
            uid.setLevelId(defaultLevel.getId());

            // 第三方信息
            JSONObject jsonObject = JSON.parseObject(json);
            //获得昵称，头像
            String name = jsonObject.getString("name");
            String profile_image_url = jsonObject.getString("avatar_url");
            // 这个service查询的
            uid.setNickname(name);
            uid.setHeader(profile_image_url);

            this.save(uid);
        } else {
            //2 否则更新令牌等信息并返回
            uid.setAccessToken(socialUserTo.getAccess_token());
            uid.setSocialUid(socialUserTo.getId());
            uid.setExpiresIn(socialUserTo.getExpires_in());
            uid.setHeader(socialUserTo.getAvatar_url());
            uid.setNickname(socialUserTo.getName());
            this.updateById(uid);
        }
        return uid;
    }
}
