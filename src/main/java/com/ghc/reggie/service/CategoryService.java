package com.ghc.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghc.reggie.bean.Category;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/7/25 - 23:30
 */
public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
