package com.John.reggie.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.John.reggie.Common.BaseContext;
import com.John.reggie.Common.R;
import com.John.reggie.dto.OrdersDto;
import com.John.reggie.entity.OrderDetail;
import com.John.reggie.entity.Orders;
import com.John.reggie.service.OrderDetailService;
import com.John.reggie.service.OrdersService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@RestController
@RequestMapping("/order")
public class OrdersController{
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders order){
        ordersService.saveOrder(order);
        return R.success("Order Submitted");
    }

    @SuppressWarnings("rawtypes")
    @GetMapping("/userPage")
    public R<Page> page(int page, int pageSize){
        Long userId = BaseContext.getCurrentId();

        Page<Orders> p = new Page<>(page,pageSize);

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId,userId);
        queryWrapper.orderByDesc(Orders::getCheckoutTime);
        ordersService.page(p,queryWrapper);

        // Convert Orders to OrdersDto
        Page<OrdersDto> pDto = new Page<>();
        BeanUtils.copyProperties(p, pDto,"records");
        List<Orders> records = p.getRecords();
        List<OrdersDto> list = records.stream().map((item)->{
            // Create new orderDto
            OrdersDto orderDto = new OrdersDto();
            BeanUtils.copyProperties(item, orderDto);
            // Get orderId
            Long orderId = item.getId();
            // Get orderDetails
            LambdaQueryWrapper<OrderDetail> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(OrderDetail::getOrderId,orderId);
            List<OrderDetail> orderDetails = orderDetailService.list(queryWrapper2);
            orderDto.setOrderDetails(orderDetails);

            return orderDto;
        }).collect(Collectors.toList());

        pDto.setRecords(list);
        return R.success(pDto);
    }

}
