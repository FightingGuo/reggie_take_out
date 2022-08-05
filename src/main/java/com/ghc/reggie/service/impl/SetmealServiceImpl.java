package com.ghc.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghc.reggie.bean.*;
import com.ghc.reggie.dto.SetmealDto;
import com.ghc.reggie.exception.CustomException;
import com.ghc.reggie.mapper.SetmealMapper;
import com.ghc.reggie.service.CategoryService;
import com.ghc.reggie.service.SetmealDishService;
import com.ghc.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/7/26 - 17:14
 */
@Service
@Slf4j
/**
 * 涉及到同时操作两张表的，都需要加上@Transactional注解
 */
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;


    /**
     * 批量修改套餐状态
     * @param st
     * @param ids
     */
    @Override
    @Transactional
    public void updateStatus(Integer st, String ids) {
        ArrayList<Setmeal> setmeals = new ArrayList<>();
        String[] strIds = ids.split(",");
        for (String item:strIds) {
            Setmeal setmeal = this.getById(item);
            setmeal.setStatus(st);
            setmeals.add(setmeal);
        }

        this.updateBatchById(setmeals);
    }

    /**
     * 批量删除套餐数据
     * @param ids
     */
    @Override
    @Transactional
    public void deleteSetmeal(String ids) {
        String[] strIds = ids.split(",");
        LambdaQueryWrapper<SetmealDish> setMealLqw=new LambdaQueryWrapper<>();
        for (String item:strIds) {
            Setmeal stmeal = this.getById(item);
            if (stmeal.getStatus()==0) {
                this.removeById(item);
                //id对饮setMeal_dish里的setMeal_id所以也要删除setMeal_dish里的信息
                setmealDishService.remove(setMealLqw);
                setMealLqw.eq(SetmealDish::getDishId,item);
            }else {
                //判断如果当前菜品的状态为1 在售状态则返回给用户不能删除
                throw new CustomException("当前套餐正在售卖中，不能删除！");
            }

        }
    }

    /**
     * 新增套餐，包含菜品信息
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveSetmealWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息 setmeal表
        this.save(setmealDto);

        //保存和套餐关联的菜品信息  setmeal_dish 表

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            //手动把id添加到setmeal_dish表种
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //saveBatch保存集合
        setmealDishService.saveBatch(setmealDishes);

    }

    @Override
    @Transactional
    public SetmealDto getByIdWithsetmealDishes(Long id) {
        //查询套餐基本信息
        Setmeal setmeal = this.getById(id);
        //创建返回的套餐类
        SetmealDto setmealDto=new SetmealDto();

        //把套餐基本信息放入setmealDto
        BeanUtils.copyProperties(setmeal,setmealDto);

        //查询id对应的菜品信息 setmealDishService   setmeal的id对应setmeal_dish表里的setmeal_id
        LambdaQueryWrapper<SetmealDish> setmealLqw=new LambdaQueryWrapper<>();
        setmealLqw.eq(SetmealDish::getDishId,id);
        List<SetmealDish> list = setmealDishService.list(setmealLqw);

        //把查出的菜品信息给setmealDto
        setmealDto.setSetmealDishes(list);

        return setmealDto;
    }

}
