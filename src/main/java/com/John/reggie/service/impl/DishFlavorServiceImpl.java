package com.John.reggie.service.impl;

import org.springframework.stereotype.Service;

import com.John.reggie.entity.DishFlavor;
import com.John.reggie.mapper.DishFlavorMapper;
import com.John.reggie.service.DishFlavorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper,DishFlavor> implements DishFlavorService{

}
