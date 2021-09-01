package henu.soft.common.exception;

import henu.soft.common.constant.ProductConstant;

public enum BizCodeEnume {

    UNKONW_EXCEPTION(10000,"未知异常！"),

    VAILD_EXCEPTION(10001,"参数格式校验失败！"),

    PRODUCT_UP_EXCEPTION(11000,"商品上架ES异常！"),
    // 注册服务异常
    SMS_CODE_EXCEPTION(20001,"请稍后在获取验证码！"),
    USER_EXIST_EXCEPTION(20002,"用户名已存在！"),
    PHONE_EXIST_EXCEPTION(20003,"手机号已存在！"),
    // 登录异常
    LOGINACCT_PASSWORD_EXCEPTION (20004,"登录密码错误！"),
    // 提交订单库存异常
    NO_STOCK_EXCEPTION(3000,"商品无库存！"),

    // Sentinel限流、熔断
    TO_MANY_REQUEST(5000,"请求太多,请稍后再试！");
    private int code;
    private String msg;

    BizCodeEnume(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
