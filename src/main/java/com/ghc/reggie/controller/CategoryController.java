package com.ghc.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghc.reggie.bean.Category;
import com.ghc.reggie.service.CategoryService;
import com.ghc.reggie.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/7/25 - 23:36
 */

/**
 * 分类管理
 */
@RestController
@Slf4j
@ResponseBody
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> addCategory(@RequestBody Category category){
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize){

//        log.info("page={} pageSize={} name={}",page,pageSize,name);

        //构造分页构造器
        Page<Category> pg=new Page<>(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Category> lqw=new LambdaQueryWrapper<>();

        //添加排序条件
        lqw.orderByAsc(Category::getSort);

        //执行查询
        categoryService.page(pg, lqw);
        //查询完后会自动封装在Page对象里

        return R.success(pg);
    }

    /**
     * 根据id删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> deleteCategory(Long id){
        categoryService.remove(id);
        return R.success("删除成功");
    }

    /**
     * 根据id修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> updateCategory(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改分类成功");
    }

    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> getCategoryByType(Category category){
        LambdaQueryWrapper<Category> categoryLqw=new LambdaQueryWrapper<>();
        categoryLqw.eq(category.getType()!=null,Category::getType,category.getType());

        //添加排序条件
        categoryLqw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(categoryLqw);

        return R.success(list);
    }
}
