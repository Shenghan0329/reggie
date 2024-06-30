package com.John.reggie.service.impl;

import org.springframework.stereotype.Service;

import com.John.reggie.entity.OrderDetail;
import com.John.reggie.mapper.OrderDetailMapper;
import com.John.reggie.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService{

}
