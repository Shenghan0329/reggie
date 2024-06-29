package com.John.reggie.service.impl;

import org.springframework.stereotype.Service;

import com.John.reggie.entity.ShoppingCart;
import com.John.reggie.mapper.ShoppingCartMapper;
import com.John.reggie.service.ShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService{

}
