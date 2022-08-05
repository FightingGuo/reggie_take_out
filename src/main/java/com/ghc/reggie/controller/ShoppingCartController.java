package com.ghc.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ghc.reggie.bean.ShoppingCart;
import com.ghc.reggie.service.ShoppingCartService;
import com.ghc.reggie.utils.BaseContext;
import com.ghc.reggie.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/8/4 - 17:33
 */

@RestController
@ResponseBody
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 将菜品或者套餐加入购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> saveObToCart(@RequestBody ShoppingCart shoppingCart){

        //获取用户id，指定是哪个用户的购物车
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        //创建条件
        LambdaQueryWrapper<ShoppingCart> shoppingCartLqw=new LambdaQueryWrapper<>();
        //把两个查询都共同需要的userId传入
        shoppingCartLqw.eq(ShoppingCart::getUserId,userId);

        //查询当前添加进来的菜品或套餐有没有在购物车里
        Long dishId = shoppingCart.getDishId();
        if (dishId!=null){ //当前添加的是菜品
            //通过userId和dishId来查询菜品
            shoppingCartLqw.eq(ShoppingCart::getDishId,dishId);

        }else {
            //当前添加的是套餐
            shoppingCartLqw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        //这里发的sql是  select * from shopping_cart where user_id=? and dish_id=?/setmeal_id=?
        ShoppingCart cartServiceOne= shoppingCartService.getOne(shoppingCartLqw);

        //如果存在shopping_cart表里的number+1
        if (cartServiceOne!=null){
            cartServiceOne.setNumber(cartServiceOne.getNumber()+1);
            //往表里+1
            shoppingCartService.updateById(cartServiceOne);
        }else {
            //不存在 才插入数据
            //因为前端并没有传回number,但是数据库中number默认值为1  所以不添加也可以
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            //存入数据库后id已经赋上值了  所以在把
            cartServiceOne=shoppingCart;

        }

        return R.success(cartServiceOne);
    }


    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> getShoppingCart(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartLqw=new LambdaQueryWrapper<>();
        shoppingCartLqw.eq(ShoppingCart::getUserId,userId);
        //根据创建时间升序排列
        shoppingCartLqw.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(shoppingCartLqw);

        return R.success(shoppingCartList);
    }



    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartLqw=new LambdaQueryWrapper<>();
        shoppingCartLqw.eq(ShoppingCart::getUserId,userId);
        shoppingCartLqw.eq(shoppingCart.getDishId()!=null,ShoppingCart::getDishId,shoppingCart.getDishId());
        //根据dishId和userId查到shoppingCart表里对应的菜品或套餐
        ShoppingCart cartServiceOne = shoppingCartService.getOne(shoppingCartLqw);
        //把数量-1
        if (cartServiceOne.getNumber()>0){ //当数量>0时才能减  否则会出现负数
            cartServiceOne.setNumber(cartServiceOne.getNumber()-1);
        }
        //当数量减到0时，当前菜品删除
        //if和else if的区别 当if和else if联用的时候它俩时有联系的 只有当if条件不满足时才会执行eles if语句
        //一开始下面语句我用else if 发现前端虽然页面上没有数量 但是购物车里有菜品虽说数量=0 原来时进不到这个else if里  算是踩坑
        if (cartServiceOne.getNumber()==0){
            shoppingCartService.remove(shoppingCartLqw);
        }

        //更新到数据库
        shoppingCartService.updateById(cartServiceOne);

        return R.success(cartServiceOne);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartLqw=new LambdaQueryWrapper<>();
        shoppingCartLqw.eq(ShoppingCart::getUserId,userId);
        shoppingCartService.remove(shoppingCartLqw);

        return R.success("清空购物车");
    }


}
