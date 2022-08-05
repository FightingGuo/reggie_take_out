package com.ghc.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ghc.reggie.bean.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/7/25 - 23:29
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
