package henu.soft.common.constant;

public class WareConstant {
    // 采购需求状态
    public enum  PurchaseStatEnum {
        CREATED(0,"新建状态"),
        ASSIGNED(1,"已分配"),
        REVEIVE(2,"已领取"),
        FINISH(3,"已完成"),
        HASERROR(4,"有异常");


        private int code;
        private String msg;

        PurchaseStatEnum(int code,String msg){
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

    // 采购单状态
    public enum  PurchaseDetailStatEnum {
        CREATED(0,"新建状态"),
        ASSIGNED(1,"已分配"),
        REVEIVE(2,"正在采购"),
        FINISH(3,"已完成"),
        HASERROR(4,"采购失败");


        private int code;
        private String msg;

        PurchaseDetailStatEnum(int code,String msg){
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
}
