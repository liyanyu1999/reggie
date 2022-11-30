package com.limeare.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.limeare.reggie.common.BaseContext;
import com.limeare.reggie.common.R;
import com.limeare.reggie.dto.OrdersDto;
import com.limeare.reggie.entity.Dish;
import com.limeare.reggie.entity.OrderDetail;
import com.limeare.reggie.entity.Orders;
import com.limeare.reggie.entity.User;
import com.limeare.reggie.service.OrderDetailService;
import com.limeare.reggie.service.OrderService;
import com.limeare.reggie.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserService userService;

    //下单
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){

        orderService.submit(orders);

        return R.success("下单成功");
    }


    //获取订单信息
    @GetMapping("/userPage")
    public R<Page> userPage(int page,int pageSize){
        //get user id
        Long userId = BaseContext.getCurrentId();

        Page<Orders> pageInfo=new Page<>(page,pageSize);
        Page<OrdersDto> ordersDtoPage=new Page<>();

        //query order from table orders
        LambdaQueryWrapper<Orders> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.eq(Orders::getUserId,userId);
        queryWrapper.orderByDesc(Orders::getCheckoutTime);
        Page<Orders> ordersPage = orderService.page(pageInfo, queryWrapper);

        //query orderDetails from table order_detail
        LambdaQueryWrapper<OrderDetail> queryWrapper1=new LambdaQueryWrapper<>();
//        queryWrapper1.eq(Orders::getId,);

        //copy
        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");

        //
        List<Orders> records = pageInfo.getRecords();

        List<OrdersDto> list=records.stream().map((item)->{
            OrdersDto ordersDto=new OrdersDto();

            BeanUtils.copyProperties(item,ordersDto);

//            ordersDto.setUserName(item.getUserName());
//            ordersDto.setPhone(item.getPhone());
//            ordersDto.set

            return ordersDto;
        }).collect(Collectors.toList());


        ordersDtoPage.setRecords(list);
        return R.success(ordersDtoPage);
    }
}
