package com.ghc.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghc.reggie.bean.Category;
import com.ghc.reggie.bean.Dish;
import com.ghc.reggie.bean.Setmeal;
import com.ghc.reggie.dto.DishDto;
import com.ghc.reggie.dto.SetmealDto;
import com.ghc.reggie.service.CategoryService;
import com.ghc.reggie.service.SetmealService;
import com.ghc.reggie.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/7/27 - 18:13
 */
@RestController
@RequestMapping("/setmeal")
@ResponseBody
@Slf4j
@Api(tags = "套餐相关接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询套餐
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "分页数据接口")
    public R<Page> page(Integer page,Integer pageSize,String name){
        //因为页面需要categoryName属性，而我们setmeal对象里只有categoryId属性，所以页面展示不了菜品分类的属性
        //此时我们需要setmealDto
        Page<Setmeal> setmealPg=new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPg=new Page<>(page,pageSize);

        LambdaQueryWrapper<Setmeal> lqw=new LambdaQueryWrapper<>();

        //添加查询条件
        lqw.like(StringUtils.isNotEmpty(name),Setmeal::getName,name);

        //添加排序条件
        lqw.orderByDesc(Setmeal::getUpdateTime);

        //执行查询
        setmealService.page(setmealPg, lqw);


        //对象拷贝 因为setmealDto继承自setmeal所以setmeal的属性 setmealDto也有 把setmealPg里的属性拷贝到setmealDtoPg
        BeanUtils.copyProperties(setmealPg,setmealDtoPg,"records");//忽略records属性

        List<Setmeal> records = setmealPg.getRecords();

        List<SetmealDto> list=records.stream().map((item)->{
            SetmealDto setmealDto=new SetmealDto();

            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId = item.getCategoryId();
            //根据id去查category表 查出对应id的name
            Category category = categoryService.getById(categoryId);

            if (category!=null){
                String categoryName = category.getName();
                //把查出的categoryName放入dishDto对象里的catrgory属性
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPg.setRecords(list);

        return R.success(setmealDtoPg);
    }


    /**
     * 批量修改套擦状态
     * @param st
     * @param ids
     * @return
     */
    @PostMapping("/status/{st}")
    @ApiOperation(value = "批量修改套餐状态接口")
    public R<String> updateStatus(@PathVariable Integer st,String ids){
        setmealService.updateStatus(st,ids);

        return R.success("修改状态成功");

    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation(value = "删除套餐接口")
    //当setmealCache里的数据 被增删改之后清除缓存
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> deleteDish(String ids){

        setmealService.deleteSetmeal(ids);

        return R.success("删除套餐成功");
    }

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增套擦接口")
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto){
//        log.info("set={}",setmealDto);
        setmealService.saveSetmealWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 修改套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "修改套餐接口")
    @CacheEvict(value = "setmealCache",allEntries = true) //allEntries设置清除所以数据
    public R<SetmealDto> updateSetmeal(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithsetmealDishes(id);
        return R.success(setmealDto);
    }


    /**
     * 保存
     * @return
     */
    @PutMapping
    @ApiOperation(value = "保存套餐接口")
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> updateSetmeal(@RequestBody SetmealDto setmealDto){
        setmealService.saveSetmealWithDish(setmealDto);
        return R.success("保存套餐成功");
    }

    /**
     * 获取套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @ApiOperation(value = "获取套餐数据接口")
    @Cacheable(value = "setmealCache",key ="'setmeal_'+#setmeal.categoryId+'_'+#setmeal.status")
    public R<List<Setmeal>> getSetMealDto(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> setmealLqw=new LambdaQueryWrapper<>();
        setmealLqw.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        setmealLqw.eq(setmeal.getStatus()==1,Setmeal::getStatus,setmeal.getStatus());

        List<Setmeal> setmealList = setmealService.list(setmealLqw);

        return R.success(setmealList);
    }

}
