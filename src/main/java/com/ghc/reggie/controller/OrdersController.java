package com.ghc.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghc.reggie.bean.Employee;
import com.ghc.reggie.bean.Orders;
import com.ghc.reggie.service.OrdersService;
import com.ghc.reggie.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/8/4 - 23:05
 */
@RestController
@ResponseBody
@RequestMapping("/order")
@Slf4j
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    /**
     * 生成订单
     * @return
     */
    @PostMapping("/submit")
    public R<String> submitOrder(@RequestBody Orders orders){

        ordersService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/page")
    public R<Page<Orders>> page(Integer page,Integer pageSize){

        //构造分页构造器
        Page<Orders> pg=new Page<>(page,pageSize);


        //执行查询
        ordersService.page(pg);
        //查询完后会自动封装在Page对象里

        return R.success(pg);
    }


}
