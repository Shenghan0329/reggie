package com.John.reggie.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.John.reggie.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface UserMapper extends BaseMapper<User>{

}
