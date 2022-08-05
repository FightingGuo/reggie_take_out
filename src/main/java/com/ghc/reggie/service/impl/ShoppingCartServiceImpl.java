package com.ghc.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghc.reggie.bean.ShoppingCart;
import com.ghc.reggie.mapper.ShoppingCartMapper;
import com.ghc.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/8/4 - 17:36
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
