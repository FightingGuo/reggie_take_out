package com.ghc.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ghc.reggie.bean.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/8/4 - 23:15
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
