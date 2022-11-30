package com.limeare.reggie.dto;

import com.limeare.reggie.entity.OrderDetail;
import com.limeare.reggie.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
