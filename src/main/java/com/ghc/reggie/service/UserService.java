package com.ghc.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghc.reggie.bean.User;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/7/31 - 10:07
 */
public interface UserService extends IService<User> {
    public boolean send(String phone,String code);
}
