package com.ghc.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghc.reggie.bean.Orders;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/8/4 - 23:08
 */
public interface OrdersService extends IService<Orders> {
    public void submit(Orders orders);
}
