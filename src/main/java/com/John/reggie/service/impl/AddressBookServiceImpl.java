package com.John.reggie.service.impl;

import org.springframework.stereotype.Service;

import com.John.reggie.entity.AddressBook;
import com.John.reggie.mapper.AddressBookMapper;
import com.John.reggie.service.AddressBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper,AddressBook> implements AddressBookService{

}
