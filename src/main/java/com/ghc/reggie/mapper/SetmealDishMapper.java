package com.ghc.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ghc.reggie.bean.SetmealDish;
import com.ghc.reggie.dto.SetmealDto;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/7/27 - 18:57
 */
@Mapper
public interface SetmealDishMapper extends BaseMapper<SetmealDish> {
    public SetmealDto getByIdWithsetmealDishes(Long id);
}
