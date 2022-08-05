package com.ghc.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghc.reggie.bean.Category;
import com.ghc.reggie.bean.Dish;
import com.ghc.reggie.bean.Setmeal;
import com.ghc.reggie.exception.CustomException;
import com.ghc.reggie.mapper.CategoryMapper;
import com.ghc.reggie.service.CategoryService;
import com.ghc.reggie.service.DishService;
import com.ghc.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/7/25 - 23:31
 */
@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，在删除之前判断是否分类中包含了菜品 如果包含了需要提示客户端
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLqw=new LambdaQueryWrapper<>();
        //添加查询条件
        dishLqw.eq(Dish::getCategoryId,id);

        int dishCount = dishService.count(dishLqw);


        //查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        if(dishCount>0){
            //说明分类下关联了菜品，抛出异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }


        //查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setMealLqw=new LambdaQueryWrapper<>();

        setMealLqw.eq(Setmeal::getCategoryId,id);

        int setMealCount = setmealService.count(setMealLqw);

        if (setMealCount>0){
            //说明分类下关联了套餐，抛出异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }


        //正常删除分类
        super.removeById(id);

    }
}
