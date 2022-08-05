package com.ghc.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ghc.reggie.bean.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/8/4 - 23:06
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
