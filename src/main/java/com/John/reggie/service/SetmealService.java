package com.John.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

import com.John.reggie.dto.SetmealDto;
import com.John.reggie.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmeal);
    public SetmealDto getByIdWithDishes(Long id);
    public void updateWithDishes(SetmealDto setmeal);
    public void deleteByIdWithDishes(List<Long> ids);
}

