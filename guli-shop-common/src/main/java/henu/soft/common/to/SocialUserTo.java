package henu.soft.common.to;



import lombok.Data;


/**
 * Gitee的第三方等录
 * 用以封装社交登录认证后换回的令牌等信息
 */
@Data
public class SocialUserTo {

    /**
     * 令牌
     */
    private String access_token;


    /**
     * 令牌过期时间
     */
    private long expires_in;

    /**
     * 该社交用户的唯一标识
     */
    private String id;


    /**
     * 第三方用户名称
     */
    private String name;

    /**
     * 头像
     */
    private String  avatar_url;



}
