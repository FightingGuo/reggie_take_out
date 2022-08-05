package com.ghc.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ghc.reggie.bean.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/7/26 - 17:12
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
