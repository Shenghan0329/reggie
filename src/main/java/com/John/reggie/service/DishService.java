package com.John.reggie.service;

import com.John.reggie.dto.DishDto;
import com.John.reggie.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DishService extends IService<Dish>{
    public void saveWithFlavor(DishDto dish);
    public DishDto getByIdWithFlavor(Long id);
    public void updateWithFlavor(DishDto dish);
}
