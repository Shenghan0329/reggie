package com.John.reggie.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.John.reggie.Common.R;
import com.John.reggie.dto.SetmealDto;
import com.John.reggie.entity.Category;
import com.John.reggie.entity.Setmeal;
import com.John.reggie.entity.SetmealDish;
import com.John.reggie.service.CategoryService;
import com.John.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmeal){
        setmealService.saveWithDish(setmeal);
        return R.success("ADD SETMEAL SUCCESS");
    }

    @SuppressWarnings("rawtypes")
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){

        // Get Setmeal
        // Page
        Page<Setmeal> p = new Page<>(page,pageSize);
        Page<SetmealDto> dtoP = new Page<>();
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        // Name
        queryWrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName,name);
        // Order
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(p,queryWrapper);

        // Copy dishes into dishDtos (Except categoryName)
        BeanUtils.copyProperties(p,dtoP,"records");

        List<Setmeal> records = p.getRecords();

        // Convert Dishes to correct format
        List<SetmealDto> list = records.stream().map((item)->{

            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            // retrieve setmealId and store in setMealDish from each dish
            Long categporyId = item.getCategoryId();
            Category c = categoryService.getById(categporyId);
            if(categporyId!=null){
                setmealDto.setCategoryName(c.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoP.setRecords(list);

        return R.success(dtoP);
    }

    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        SetmealDto setmeal = setmealService.getByIdWithDishes(id);
        return R.success(setmeal);
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmeal){
        setmealService.updateWithDishes(setmeal);
        return R.success("Set Meal Update Success");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.deleteByIdWithDishes(ids);
        return R.success("Delete Setmeals Success");
    }

    // Set in/out stock
    @PostMapping("/status/{status}")
    public R<String> setStatus(@RequestParam List<Long> ids, @PathVariable("status") int status){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        Setmeal meal = new Setmeal();
        meal.setStatus(status);
        setmealService.update(meal,queryWrapper);
        return R.success("Update status success");
    }
}
