package com.John.reggie.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

import jakarta.servlet.http.HttpServletRequest;

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
    @GetMapping(value = {"/userPage","/page"})
    public R<Page> page(int page, int pageSize, Long number, String beginTime, String endTime,HttpServletRequest req){
        Page<Orders> p = new Page<>(page,pageSize);

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        if(req.getRequestURL().toString().contains("user")){
            Long userId = BaseContext.getCurrentId();
            queryWrapper.eq(Orders::getUserId,userId);
        }
        if(beginTime!=null){
            beginTime = beginTime.replace(" ", "T");
            LocalDateTime start = LocalDateTime.parse(beginTime);
            queryWrapper.ge(beginTime!=null,Orders::getOrderTime,start);
        }
        if(endTime!=null){
            endTime = endTime.replace(" ", "T");
            LocalDateTime end = LocalDateTime.parse(endTime);
            queryWrapper.le(endTime!=null,Orders::getOrderTime,end);
        }
        queryWrapper.eq(number!=null,Orders::getId,number);
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
