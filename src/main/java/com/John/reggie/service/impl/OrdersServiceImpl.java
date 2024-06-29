package com.John.reggie.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.John.reggie.Common.BaseContext;
import com.John.reggie.Common.CustomException;
import com.John.reggie.entity.AddressBook;
import com.John.reggie.entity.OrderDetail;
import com.John.reggie.entity.Orders;
import com.John.reggie.entity.ShoppingCart;
import com.John.reggie.entity.User;
import com.John.reggie.mapper.OrdersMapper;
import com.John.reggie.service.AddressBookService;
import com.John.reggie.service.OrderDetailService;
import com.John.reggie.service.OrdersService;
import com.John.reggie.service.ShoppingCartService;
import com.John.reggie.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService{
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Override
    public void saveOrder(Orders order) {
        // Get User info
        Long userId = BaseContext.getCurrentId();
        User user = userService.getById(userId);
        order.setUserId(userId);
        order.setUserName(user.getName());

        // Get Current ShoppingCart
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> carts = shoppingCartService.list(queryWrapper);
        if(carts == null){
            throw new CustomException("Shopping Cart Cannot Be Empty");
        }

        // Get Address info
        AddressBook address = addressBookService.getById(order.getAddressBookId());
        if(address == null){
            throw new CustomException("Addressbook Information Empty");
        }

        // Set properties
        Long orderId = IdWorker.getId();
        order.setId(orderId);
        order.setNumber(String.valueOf(orderId));
        order.setPhone(address.getPhone());
        order.setConsignee(address.getConsignee());
        order.setAddress(
            (address.getProvinceName() == null ? "" : address.getProvinceName()) 
            + (address.getCityName() == null ? "" : address.getCityName())
            + (address.getDistrictName() == null ? "" : address.getDistrictName())
            + (address.getDetail() == null ? "" : address.getDetail())
        );
        order.setStatus(2);

        // Set Order Time
        order.setOrderTime(LocalDateTime.now());
        order.setCheckoutTime(LocalDateTime.now());

        // Set Order Details
        AtomicInteger amount = new AtomicInteger(0);
        List<OrderDetail> orderDetails = carts.stream().map((item)->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());
        orderDetailService.saveBatch(orderDetails);
        order.setAmount(new BigDecimal(amount.get()));

        // Save the order
        this.save(order);

        // Empty the shopping cart
        shoppingCartService.remove(queryWrapper);
    }

}
