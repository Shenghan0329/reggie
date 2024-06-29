package com.John.reggie.dto;

import java.util.List;

import com.John.reggie.entity.OrderDetail;
import com.John.reggie.entity.Orders;

import lombok.Data;

@Data
public class OrdersDto extends Orders{
    private List<OrderDetail> orderDetails;
}
