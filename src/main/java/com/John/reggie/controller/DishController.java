package com.John.reggie.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.John.reggie.Common.R;
import com.John.reggie.dto.DishDto;
import com.John.reggie.entity.Category;
import com.John.reggie.entity.Dish;
import com.John.reggie.entity.Employee;
import com.John.reggie.service.CategoryService;
import com.John.reggie.service.DishFlavorService;
import com.John.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody DishDto dish){
        log.info(dish.toString());
        dishService.saveWithFlavor(dish);
        return R.success("DISH ADDED");
    }

    @SuppressWarnings("rawtypes")
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page = {}, pageSize = {}, name = {}",page,pageSize,name);

        // Get Employees
        // Page
        Page<Dish> p = new Page<>(page,pageSize);
        Page<DishDto> dtoP = new Page<>();
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // Name
        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName,name);
        // Order
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(p,queryWrapper);

        // Copy dishes into dishDtos (Except categoryName)
        BeanUtils.copyProperties(p,dtoP,"records");

        List<Dish> records = p.getRecords();

        // Convert Dishes to correct format
        List<DishDto> list = records.stream().map((item)->{

            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            // retrieve categoryName and store in dishDto from each dish
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dtoP.setRecords(list);

        return R.success(dtoP);
    }

    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }
    @PutMapping
    public R<String> update(@RequestBody DishDto dish){
        log.info(dish.toString());
        dishService.updateWithFlavor(dish);
        return R.success("DISH UPDATED");
    }
}
