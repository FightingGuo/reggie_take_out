package com.ghc.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghc.reggie.bean.Dish;
import com.ghc.reggie.bean.DishFlavor;
import com.ghc.reggie.dto.DishDto;
import com.ghc.reggie.exception.CustomException;
import com.ghc.reggie.mapper.DishMapper;
import com.ghc.reggie.service.DishFlavorService;
import com.ghc.reggie.service.DishService;
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
 * 2022/7/26 - 17:38
 */
@Service
@Slf4j
/**
 * 涉及到同时操作两张表的，都需要加上@Transactional注解
 */
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
     */
    @Override
    @Transactional
    public void saveDishWithFlavor(DishDto dishDto) {
        //保存菜品基本信息到dish表
        this.save(dishDto);
        //存入菜品信息后，表中自动生成dish表的id，取出id
        Long dishId = dishDto.getId();


        //保存菜品口味数据到到dish_flavor
        //因为一个菜品对应多个口味，所以循环遍历口味设置上他们的菜品id
//        List<DishFlavor> flavors = dishDto.getFlavors();
//        flavors.stream().map((item)->{
//            item.setDishId(dishId);
//            return item;
//        }).collect(Collectors.toList());

        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor item:flavors) {
            item.setDishId(dishId);
        }

        //saveBatch保存集合
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询对饮的菜品dish和口味  所以需要查两表
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品信息
        Dish dish = this.getById(id);
        Long dishId = dish.getId();

        //查询dishId对应的口味信息
        LambdaQueryWrapper<DishFlavor> dishFlavorLqw=new LambdaQueryWrapper<>();
        dishFlavorLqw.eq(DishFlavor::getDishId,dishId);

        List<DishFlavor> list = dishFlavorService.list(dishFlavorLqw);

        DishDto dishDto=new DishDto();

        BeanUtils.copyProperties(dish,dishDto);

        dishDto.setFlavors(list);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateDishAndFlavor(DishDto dishDto) {
        //因为dishDto是dish的子类，所以直接传入，会去更新dishDto数据中 dish中的字段
        this.updateById(dishDto);

        //清理当前菜品对应口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> dishFlavorLqw=new LambdaQueryWrapper<>();
        dishFlavorLqw.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(dishFlavorLqw);

        //添加当前提交过来的口味数据--- dish_flavor表的insert操作

        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor item:flavors) {
            item.setDishId(dishDto.getId());
        }

        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 批量修改
     * @param st
     * @param ids
     */
    @Override
    @Transactional
    public void updateStatus(Integer st, String ids) {
        ArrayList<Dish> dishes = new ArrayList<>();
        String[] strIds = ids.split(",");
        for (String item:strIds) {
            Dish dish = this.getById(item);
            dish.setStatus(st);
            dishes.add(dish);
        }

        this.updateBatchById(dishes);
    }

    /**
     * 批量删除，注意要删除菜品表和口味表
     * @param ids
     */
    @Override
    @Transactional
    public void deleteDishAndFlavor(String ids) {

        String[] strIds = ids.split(",");
        LambdaQueryWrapper<DishFlavor> dishFlavorLqw=new LambdaQueryWrapper<>();
        for (String item:strIds) {
            //根据id拿到菜品对象，判断当前菜品状态
            Dish dish = this.getById(item);
            //如果为0(停售状态) 则可以删除
            if (dish.getStatus()==0){
                //根据id删除dish表的菜品
                this.removeById(item);
                //根据id删除dish_flavor表对应的口味
                //因为id对应dish_flavor表的dish_id ,所以要用条件
                dishFlavorLqw.eq(DishFlavor::getDishId,item);

                dishFlavorService.remove(dishFlavorLqw);
            }else {
                throw new CustomException("当前菜品为售卖状态，不能删除!");
            }

        }
    }

}
