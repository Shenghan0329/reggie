package com.John.reggie.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.John.reggie.dto.DishDto;
import com.John.reggie.entity.Dish;
import com.John.reggie.entity.DishFlavor;
import com.John.reggie.mapper.DishMapper;
import com.John.reggie.service.CategoryService;
import com.John.reggie.service.DishFlavorService;
import com.John.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService{

    @Autowired
    private DishFlavorService dishFlavorService;
    // @Autowired
    // private CategoryService categoryService;

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dish) {
        this.save(dish);

        Long dishId = dish.getId();

        List<DishFlavor> dishFlavors = dish.getFlavors();
        dishFlavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(dishFlavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish d = this.getById(id);
        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(d,dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, d.getId());
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(list);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dish) {
        this.updateById(dish);

        Long dishId = dish.getId();

        // remove old dish flavor
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        dishFlavorService.remove(queryWrapper);

        // insert new dish flavor
        List<DishFlavor> dishFlavors = dish.getFlavors();
        dishFlavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(dishFlavors);
    }

    @Override
    public List<DishDto> getWithFlavor(List<Dish> dish) {
        List<DishDto> list = dish.stream().map((item)->{
            DishDto dto = new DishDto();
            BeanUtils.copyProperties(item, dto);
            Long dishId = item.getId();

            // Get Flavors
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
            dto.setFlavors(flavors);

            // // Get Category Name
            // Long categoryId = item.getCategoryId();
            // String categoryName = categoryService.getById(categoryId).getName();
            // dto.setCategoryName(categoryName);
            
            return dto;
        }).collect(Collectors.toList());

        return list;
    }

    
}
