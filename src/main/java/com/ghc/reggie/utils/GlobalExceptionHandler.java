package com.ghc.reggie.utils;

import com.ghc.reggie.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/7/24 - 19:41
 */

/**
 * 全局异常捕获处理器
 */
@Slf4j
@ResponseBody
@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
//        log.error(ex.getMessage());

        //判断异常信息里是否包含Duplicate entry关键字，如果用则可以进一步提示
        if (ex.getMessage().contains("Duplicate entry")) {
            /**
             * split(regex,limit)
             * 例：String str=",a,s,,d,3,5,g,a, , ,”
             * 第一个规则：输入limit为数字1,切割执行1-1次 ,也就是0次,所以切割后的数组长度仍然是1,也就是原来的字符串
             * 第二个规则：如果输入的limit数值是非正数,则执行切割到无限次,数组长度也可以是任何数值 切完后数组长度12  空,a,s,空,d,3,5,g,a,空,空,空
             * 第三个规则：如果输入limit数值等于0,则会执行切割无限次并且去掉该数组最后的所有空字符串 切完后数组长度9  空,a,s,空,d,3,5,g,a
             */
            String[] spit = ex.getMessage().split(" ");
            String msg = "用户名" + spit[2] + "已存在,请重新输入";
            return R.error(msg);
        }

        return R.error("---出现未知错误！！---");
    }


    /**
     * 自定义异常类的，异常处理方法
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex) {
//        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }

}
