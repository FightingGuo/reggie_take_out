package com.ghc.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghc.reggie.bean.*;
import com.ghc.reggie.exception.CustomException;
import com.ghc.reggie.mapper.OrdersMapper;
import com.ghc.reggie.service.*;
import com.ghc.reggie.utils.BaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/8/4 - 23:08
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单，生成订单
     * @param orders
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        //获取当前用户id
        Long userId = BaseContext.getCurrentId();

        //获取当前用户的购物车
        LambdaQueryWrapper<ShoppingCart> shoppingCartLqw=new LambdaQueryWrapper<>();
        shoppingCartLqw.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(shoppingCartLqw);

        if (shoppingCartList==null || shoppingCartList.size()==0){
            throw new CustomException("购物车为空");
        }

        //查询用户数据
        User user = userService.getById(userId);

        //查询地址
        AddressBook address = addressBookService.getById(orders.getAddressBookId());
        if (address==null){
            throw new CustomException("地址信息为空,不能下单");
        }

        long orderId = IdWorker.getId();//生成一个随机订单号

        //原子操作，保证线程安全
        AtomicInteger amount=new AtomicInteger(0);


        //遍历购物车 给订单明细赋值   累加算出 amount
        List<OrderDetail> orderDetailList=shoppingCartList.stream().map((item)->{
            OrderDetail orderDetail=new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        //下单其实就是向订单表插入一条数据
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(address.getConsignee());
        orders.setPhone(address.getPhone());
        orders.setAddress((address.getProvinceName() == null ? "" : address.getProvinceName())
                + (address.getCityName() == null ? "" : address.getCityName())
                + (address.getDistrictName() == null ? "" : address.getDistrictName())
                + (address.getDetail() == null ? "" : address.getDetail()));

        this.save(orders);

        //向订单明细表插入数据，可能是多条数据
        orderDetailService.saveBatch(orderDetailList);

        //清空购物车数据
        shoppingCartService.remove(shoppingCartLqw);

    }
}
