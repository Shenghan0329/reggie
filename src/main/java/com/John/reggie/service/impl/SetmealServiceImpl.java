package com.John.reggie.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.mapping.Set;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.John.reggie.Common.CustomException;
import com.John.reggie.dto.SetmealDto;
import com.John.reggie.entity.Setmeal;
import com.John.reggie.entity.SetmealDish;
import com.John.reggie.mapper.SetmealMapper;
import com.John.reggie.service.SetmealDishService;
import com.John.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import jakarta.transaction.Transactional;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService{
    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmeal) {
        this.save(setmeal);

        List<SetmealDish> dishes = setmeal.getSetmealDishes();
        Long setMealId = setmeal.getId();
        List<SetmealDish> list = dishes.stream().map((item)->{
            item.setSetmealId(setMealId);
            return item;
        }).collect(Collectors.toList());
        
        setmealDishService.saveBatch(list);
    }

    @Override
    public SetmealDto getByIdWithDishes(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);
        Long setmealId = setmealDto.getId();
        if(setmealId!=null){
            LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SetmealDish::getSetmealId,setmealId);
            queryWrapper.orderByAsc(SetmealDish::getSort).orderByDesc(SetmealDish::getUpdateTime);
            List<SetmealDish> list = setmealDishService.list(queryWrapper);
            setmealDto.setSetmealDishes(list);
        }
        return setmealDto;
    }

    @Override
    @Transactional
    public void updateWithDishes(SetmealDto setmeal) {
        this.updateById(setmeal);

        Long setMealId = setmeal.getId();

        // remove old dishes
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setMealId);
        setmealDishService.remove(queryWrapper);

        // insert new setmeal dishes
        List<SetmealDish> dishes = setmeal.getSetmealDishes();
        dishes.stream().map((item)->{
            item.setSetmealId(setMealId);
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(dishes);
    }

    @Override
    @Transactional
    public void deleteByIdWithDishes(List<Long> ids) {
        // Cannot delete if on stock
        LambdaQueryWrapper<Setmeal> qWrapper = new LambdaQueryWrapper<>();
        qWrapper.in(Setmeal::getId,ids);
        qWrapper.eq(Setmeal::getStatus,1);
        Long count = this.count(qWrapper);
        if(count > 0) 
            throw new CustomException("Setmeal on stock, cannot delete");
        
        // Delete items
        this.removeByIds(ids);

        // delete related dishes
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper);
    }
    

}
