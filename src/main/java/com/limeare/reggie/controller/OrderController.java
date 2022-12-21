package com.limeare.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.limeare.reggie.common.BaseContext;
import com.limeare.reggie.common.R;
import com.limeare.reggie.dto.OrdersDto;
import com.limeare.reggie.entity.*;
import com.limeare.reggie.enumeration.OrderStatusEnum;
import com.limeare.reggie.service.AddressBookService;
import com.limeare.reggie.service.OrderDetailService;
import com.limeare.reggie.service.OrderService;
import com.limeare.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    //下单
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){

        orderService.submit(orders);

        return R.success("下单成功");
    }


    //用户获取订单信息
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
        orderService.page(pageInfo, queryWrapper);

        //query orderDetails from table order_detail
        LambdaQueryWrapper<OrderDetail> queryWrapper1=new LambdaQueryWrapper<>();

        //copy
        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");

        //
        List<Orders> records = pageInfo.getRecords();

        List<OrdersDto> list=records.stream().map((item)->{
            OrdersDto ordersDto=new OrdersDto();

            BeanUtils.copyProperties(item,ordersDto);

            User user = userService.getById(userId);
            if (user!=null){
                String name = user.getName();
                ordersDto.setUserName(name);
            }

            Long addressBookId = item.getAddressBookId();
            AddressBook addressBook = addressBookService.getById(addressBookId);
            if (addressBook!=null){
                String detail = addressBook.getDetail();
                ordersDto.setAddress(detail);
            }

            Long orderId = item.getId();
            LambdaQueryWrapper<OrderDetail> queryWrapper2=new LambdaQueryWrapper<>();
            queryWrapper2.eq(OrderDetail::getOrderId,orderId);
            List<OrderDetail> list1 = orderDetailService.list(queryWrapper2);
            ordersDto.setOrderDetails(list1);

            return ordersDto;
        }).collect(Collectors.toList());


        ordersDtoPage.setRecords(list);
        return R.success(ordersDtoPage);
    }


    //分页查询
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number, LocalDateTime beginTime,LocalDateTime endTime){

        Page<Orders> pageInfo =new Page(page,pageSize);
        Page<OrdersDto> ordersDtoPage =new Page<>();

        LambdaQueryWrapper<Orders> queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.eq(number!=null,Orders::getNumber,number);
        queryWrapper.between(beginTime!=null,Orders::getOrderTime,beginTime,endTime);
        queryWrapper.orderByDesc(Orders::getOrderTime);

        orderService.page(pageInfo,queryWrapper);

        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");

        List<Orders> records = pageInfo.getRecords();

        List<OrdersDto> list=records.stream().map((item)->{
            OrdersDto ordersDto=new OrdersDto();

            BeanUtils.copyProperties(item,ordersDto);
            Long userId = item.getUserId();
            Long addressBookId = item.getAddressBookId();

            User user = userService.getById(userId);
            AddressBook addressBook = addressBookService.getById(addressBookId);

            if (user!=null){
                String name = user.getName();
                ordersDto.setUserName(name);
            }
            if (addressBook!=null){
                String detail = addressBook.getDetail();
                ordersDto.setAddress(detail);
            }

            return ordersDto;
        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(list);

        return R.success(ordersDtoPage);
    }

    //修改订单状态
    @PutMapping
    public R<String> updateStatus(@RequestBody Orders orders){
        orderService.updateWithStatus(orders);

        return R.success("订单状态改变成功");
    }

    //再来一单
    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders){
        Long id = orders.getId();
        orderService.again(id);
        return R.success("下单成功");
    }

    //取消订单
    @DeleteMapping("/delete")
    public R<String> delete(@RequestBody Orders orders){
        Long id = orders.getId();
        orders.setStatus(OrderStatusEnum.STATUS_5.getValue());
        if(!orderService.saveOrUpdate(orders)){
            return R.error("系统忙，请稍后重试");
        }
        return R.success("订单已取消");

    }
}
