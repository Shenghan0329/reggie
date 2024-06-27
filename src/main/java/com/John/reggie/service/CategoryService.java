package com.John.reggie.service;

import com.John.reggie.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CategoryService extends IService<Category>{
    public void remove(Long id);
}
