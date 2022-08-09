package com.ghc.reggie.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/7/24 - 9:02
 */

/**
 * 通用返回结果，服务端响应的数据最终都会封装成此对象
 * @param <T>
 */
@Data
@ApiModel("返回结果")
public class R<T> implements Serializable {
    @ApiModelProperty("编码")
    private Integer code; //编码:1成功，0和其他表示不成功

    @ApiModelProperty("错误信息")
    private String msg; //错误信息

    @ApiModelProperty("数据")
    private T data; //数据

    @ApiModelProperty("动态数据")
    private HashMap map=new HashMap();//动态数据，返回后端json数据给前端

    public static <T> R<T> success(T object){
        R<T> r=new R<T>();
        r.setCode(1);
        r.setData(object);
        return r;
    }

    public static <T> R<T> error(String msg){
        R<T> r=new R<T>();
        r.setCode(0);
        r.setMsg(msg);
        return r;
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
}
