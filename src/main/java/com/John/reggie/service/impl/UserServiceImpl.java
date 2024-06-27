package com.John.reggie.service.impl;

import org.springframework.stereotype.Service;

import com.John.reggie.entity.User;
import com.John.reggie.mapper.UserMapper;
import com.John.reggie.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService{

}
