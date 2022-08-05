package com.ghc.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ghc.reggie.bean.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/8/4 - 17:34
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
