package com.ghc.reggie.exception;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/7/26 - 18:58
 */

/**
 * 自定义义务异常类
 */
public class CustomException extends RuntimeException{

    public CustomException(String message) {
        super(message);
    }
}
