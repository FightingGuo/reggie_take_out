package com.ghc.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghc.reggie.bean.Dish;
import com.ghc.reggie.dto.DishDto;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/7/26 - 17:13
 */
public interface DishService extends IService<Dish> {
    public void saveDishWithFlavor(DishDto dishDto);
    public DishDto getByIdWithFlavor(Long id);
    public void updateDishAndFlavor(DishDto dishDto);

    public void updateStatus(Integer st, String ids);

    public void deleteDishAndFlavor(String ids);
}
