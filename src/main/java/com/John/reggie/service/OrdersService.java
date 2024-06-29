package com.John.reggie.service;

import com.John.reggie.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OrdersService extends IService<Orders>{
    public void saveOrder(Orders order);
}
