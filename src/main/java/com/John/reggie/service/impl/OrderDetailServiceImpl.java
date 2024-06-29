package com.John.reggie.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.John.reggie.Common.BaseContext;
import com.John.reggie.Common.CustomException;
import com.John.reggie.entity.AddressBook;
import com.John.reggie.entity.OrderDetail;
import com.John.reggie.entity.ShoppingCart;
import com.John.reggie.entity.User;
import com.John.reggie.mapper.OrderDetailMapper;
import com.John.reggie.service.OrderDetailService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService{

}
