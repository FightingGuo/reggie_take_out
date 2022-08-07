package com.ghc.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghc.reggie.bean.Category;
import com.ghc.reggie.bean.Dish;
import com.ghc.reggie.bean.DishFlavor;
import com.ghc.reggie.dto.DishDto;
import com.ghc.reggie.service.CategoryService;
import com.ghc.reggie.service.DishFlavorService;
import com.ghc.reggie.service.DishService;
import com.ghc.reggie.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/7/26 - 18:18
 */
@ResponseBody
@RestController
@RequestMapping("/dish")
@Slf4j
/**
 * 菜品管理
 */
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize,String name){

//        log.info("page={} pageSize={} name={}",page,pageSize,name);

        //构造分页构造器
        //因为页面需要categoryName属性，而我们dish对象里只有categoryId属性，所以页面展示不了菜品分类的属性
        //此时我们需要dishDto
        Page<Dish> dishPage=new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage=new Page<>(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Dish> lqw=new LambdaQueryWrapper<>();

        //添加查询条件
        lqw.like(StringUtils.isNotEmpty(name),Dish::getName,name);

        //添加排序条件
        lqw.orderByAsc(Dish::getUpdateTime);

        //执行查询
        dishService.page(dishPage, lqw);
        //对象拷贝 因为dishDto继承自dish所以dish的属性 dishDto也有 把dishPage里的属性拷贝到dishDtoPage
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");//忽略records属性

        List<Dish> records = dishPage.getRecords();

        List<DishDto> list=records.stream().map((item)->{
            DishDto dishDto=new DishDto();

            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            //根据id去查category表 查出对应id的name
            Category category = categoryService.getById(categoryId);

            if (category!=null){
                String categoryName = category.getName();
                //把查出的categoryName放入dishDto对象里的catrgory属性
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }


    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> saveDish(@RequestBody DishDto dishDto){
        dishService.saveDishWithFlavor(dishDto);

        //新增菜品之后需要把redis的缓存做一个清理
        String key="dish_"+dishDto.getCategoryId()+"_"+dishDto.getStatus(); //根据原先命名key的方式拿到key

        redisTemplate.delete(key);

        return R.success("新增菜品成功");
    }


    /**
     * 根据id获取菜品信息和口味
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getDishDtoById(@PathVariable Long id){
//        log.info(id.toString());
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }


    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> updateDishAndFlavor(@RequestBody DishDto dishDto){
        dishService.updateDishAndFlavor(dishDto);

        //修改菜品之后也需要把redis的缓存做一个清理
        String key="dish_"+dishDto.getCategoryId()+"_"+dishDto.getStatus(); //根据原先命名key的方式拿到key

        redisTemplate.delete(key);

        return R.success("修改菜品成功");
//        return null;
    }

    /**
     * 停售启售菜品  传回后端的ids为string类型，在后端进行一个
     * @param st
     * @param ids
     * @return
     */
    @PostMapping("/status/{st}")
    public R<String> updateStatus(@PathVariable Integer st,String ids){
//        log.info("st={}  ids={}",st,ids);
        dishService.updateStatus(st,ids);

        return R.success("修改状态成功");

    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteDish(String ids){

        dishService.deleteDishAndFlavor(ids);
        return R.success("删除菜品成功");
    }


    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList=null;

        //动态构造key
        String key="dish_"+dish.getCategoryId()+"_"+dish.getStatus();

        //先从redis中获取缓存数据
         dishDtoList= (List<DishDto>) redisTemplate.opsForValue().get(key);

        //如果存在直接返回数据
        if (dishDtoList!=null) {
            return R.success(dishDtoList);
        }
        //如果不存在再取查询数据库

            //构造查询对象
            LambdaQueryWrapper<Dish> dishLqw = new LambdaQueryWrapper<>();
            dishLqw.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());

            //有个点   只查状态为1的也就是在售状态的菜品
            dishLqw.eq(Dish::getStatus, 1);

            //添加排序条件
            dishLqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
            List<Dish> list = dishService.list(dishLqw);


            dishDtoList=list.stream().map((item) -> {
                DishDto dishDto = new DishDto();

                BeanUtils.copyProperties(item, dishDto);
                Long categoryId = item.getCategoryId();
                //根据id去查category表 查出对应id的name
                Category category = categoryService.getById(categoryId);

                if (category != null) {
                    String categoryName = category.getName();
                    //把查出的categoryName放入dishDto对象里的category属性
                    dishDto.setCategoryName(categoryName);
                }

                //根据id查询分类对象
                Long dishId = item.getId();

                LambdaQueryWrapper<DishFlavor> dishFlavorLqw = new LambdaQueryWrapper<>();
                dishFlavorLqw.eq(DishFlavor::getDishId, dishId);
                List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorLqw);
                dishDto.setFlavors(dishFlavorList);

                return dishDto;
            }).collect(Collectors.toList());


        //把查到的数据缓存在redis中  设置过期时间60分钟
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }


}
