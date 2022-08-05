package com.ghc.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghc.reggie.bean.Setmeal;
import com.ghc.reggie.dto.SetmealDto;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/7/26 - 17:14
 */
public interface SetmealService extends IService<Setmeal> {

    public void updateStatus(Integer st, String ids);

    public void deleteSetmeal(String ids);

    public void saveSetmealWithDish(SetmealDto setmealDto);

    @Transactional
    SetmealDto getByIdWithsetmealDishes(Long id);
}
