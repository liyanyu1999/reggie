package com.limeare.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.limeare.reggie.common.BaseContext;
import com.limeare.reggie.common.CustomerException;
import com.limeare.reggie.entity.*;
import com.limeare.reggie.mapper.OrderMapper;
import com.limeare.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {


    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    //下单
    @Override
    @Transactional
    public void submit(Orders orders) {
        //获取当前用户
        Long userId = BaseContext.getCurrentId();

        //查询购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);

        if (shoppingCarts==null || shoppingCarts.size()==0){
            throw new CustomerException("购物车里没有商品");
        }

        //获取信息
        User user = userService.getById(userId);

        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);

        if (addressBook==null){
            throw new CustomerException("请填写收货地址");
        }

        long orderId = IdWorker.getId();//订单号

        //插入订单
        AtomicInteger amount=new AtomicInteger(0);
        //记录订单详细信息
        List<OrderDetail> orderDetails=shoppingCarts.stream().map((item)->{

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

        //设置订单信息
        orders.setNumber(String.valueOf(orderId));
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserId(userId);
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName()==null ? "": addressBook.getProvinceName())
                +(addressBook.getCityName()==null ? "" : addressBook.getCityName())
                +(addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                +(addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        this.save(orders);

        //插入订单明细
        orderDetailService.saveBatch(orderDetails);

        //清空购物车
        shoppingCartService.remove(queryWrapper);

    }

    //更改订单状态
    @Override
    public void updateWithStatus(Orders orders) {
        this.updateById(orders);
    }

    //再来一单
    @Override
    public void again(Long id) {

        LambdaQueryWrapper<Orders> queryWrapper=new LambdaQueryWrapper<>();
        LambdaQueryWrapper<OrderDetail> queryWrapper1=new LambdaQueryWrapper<>();

        queryWrapper.eq(Orders::getId,id);
        Orders orders = this.getOne(queryWrapper);

        long orderId = IdWorker.getId();

        Orders againOrder =new Orders();

        queryWrapper1.eq(OrderDetail::getOrderId,id);
        //订单详细信息
        List<OrderDetail> orderDetails = orderDetailService.list(queryWrapper1).stream().map((item)->{

            OrderDetail orderDetail=new OrderDetail();

            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());

            return orderDetail;

        }).collect(Collectors.toList());

        againOrder.setNumber(String.valueOf(orderId));
        againOrder.setId(orderId);
        againOrder.setOrderTime(LocalDateTime.now());
        againOrder.setCheckoutTime(LocalDateTime.now());
        againOrder.setStatus(2);
        againOrder.setAmount(orders.getAmount());
        againOrder.setUserId(orders.getUserId());
        againOrder.setUserName(orders.getUserName());
        againOrder.setConsignee(orders.getConsignee());
        againOrder.setPhone(orders.getPhone());
        againOrder.setAddressBookId(orders.getAddressBookId());
        againOrder.setAddress(orders.getAddress());

        this.save(againOrder);

        orderDetailService.saveBatch(orderDetails);

    }


}
