package com.ghc.reggie.utils;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/7/25 - 22:22
 */

/**
 * 问题引出:在MyMetaObjectHandler中updateFill无法动态获取当前登录用户的id  因为无法使用HttpServletRequest
 *  update保存请求======>LoginCheckFilter======>EmployeeController=======>MyMetaObjectHandler
 * 由于每发一次http请求都是由一条线程处理 所以可以在LonginCheckFilter存储id的信息在MyMetaObjectHandler取出
 *
 * 1、基于ThreadLocal封装的工具类
 */
public class BaseContext {
   private static ThreadLocal<Long> threadLocal=new InheritableThreadLocal<>();

   public static void setCurrentId(Long id){
       threadLocal.set(id);
   }

    public static Long getCurrentId(){
       return threadLocal.get();
   }

}
