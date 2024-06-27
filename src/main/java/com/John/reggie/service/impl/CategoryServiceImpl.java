package com.John.reggie.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.John.reggie.Common.CustomException;
import com.John.reggie.entity.Category;
import com.John.reggie.entity.Dish;
import com.John.reggie.entity.Setmeal;
import com.John.reggie.mapper.CategoryMapper;
import com.John.reggie.service.CategoryService;
import com.John.reggie.service.DishService;
import com.John.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements CategoryService{

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.eq(Dish::getCategoryId, id);
        int count = (int) dishService.count(dishWrapper);
        if(count > 0){
            throw new CustomException("Cannot Delete: Exist Related Dishes");
        }
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        setmealWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = (int) setmealService.count(setmealWrapper);
        if(count2 > 0){
            throw new CustomException("Cannot Delete: Exist Related Mealsets");
        }
        super.removeById(id);
    }
    
}
