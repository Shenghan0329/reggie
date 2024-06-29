package com.John.reggie.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.John.reggie.Common.BaseContext;
import com.John.reggie.Common.R;
import com.John.reggie.entity.ShoppingCart;
import com.John.reggie.service.ShoppingCartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import aj.org.objectweb.asm.Label;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){

        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        // Query whether the item in cart
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);

        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        if(dishId != null){
            // if it is a dish
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            // if it is a setmeal
            queryWrapper.eq(setmealId!=null, ShoppingCart::getSetmealId, setmealId);
        }

        ShoppingCart s = shoppingCartService.getOne(queryWrapper);

        // If item not in cart
        if(s==null){
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            s = shoppingCart;
        }else{
            s.setNumber(s.getNumber()+1);
            shoppingCartService.updateById(s);
        }
        
        return R.success(s);
    }

    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        if(dishId!=null){
            queryWrapper.eq(dishId!=null,ShoppingCart::getDishId,dishId);
        }else{
            queryWrapper.eq(setmealId!=null, ShoppingCart::getSetmealId,setmealId);
        }
        ShoppingCart c = shoppingCartService.getOne(queryWrapper);

        if(c==null){
            return R.error("No Such Item");
        }

        int number = c.getNumber();
        if(number > 1){
            c.setNumber(c.getNumber()-1);
            shoppingCartService.updateById(c);
        }else{
            shoppingCartService.removeById(c);
            c.setNumber(0);
        }
        return R.success(c);
    }

    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return R.success("Shopping Cart Clean");
    }
}
