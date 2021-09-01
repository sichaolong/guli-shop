package henu.soft.xiaosi.product.exception;


import henu.soft.common.exception.BizCodeEnume;
import henu.soft.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "henu.soft.xiaosi.product.controller")
public class ProductControllerAdvice {


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R HandleNotValidException(MethodArgumentNotValidException e){
        // 获取所有校验失败的字段
        List<FieldError> fieldErrors = e.getFieldErrors();
        // 将提示结果放到map
        Map<String,String> resMap = new HashMap<>();
        fieldErrors.forEach((fieldError)->{
            String msg = fieldError.getDefaultMessage();
            String field = fieldError.getField();
            resMap.put(field,msg);
        });
        log.error("数据校验异常{},异常类型{}",e.getMessage(),e.getClass());
        return R.error(BizCodeEnume.VAILD_EXCEPTION.getCode(),BizCodeEnume.VAILD_EXCEPTION.getMsg()).put("data",resMap);

    }

    @ExceptionHandler(value = Throwable.class)
    public R HandleException(Throwable throwable){
        log.error(throwable.getMessage(),throwable.getClass());
        return R.error(BizCodeEnume.UNKONW_EXCEPTION.getCode(),BizCodeEnume.UNKONW_EXCEPTION.getMsg()).put("data",throwable.getMessage());

    }
}
