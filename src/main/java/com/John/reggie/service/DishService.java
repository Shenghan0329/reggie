package com.John.reggie.service;

import java.util.List;

import com.John.reggie.dto.DishDto;
import com.John.reggie.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DishService extends IService<Dish>{
    public void saveWithFlavor(DishDto dish);
    public DishDto getByIdWithFlavor(Long id);
    public void updateWithFlavor(DishDto dish);
    public List<DishDto> getWithFlavor(List<Dish> dish);
    public void deleteByIdWithFlavors(List<Long> ids);
}
