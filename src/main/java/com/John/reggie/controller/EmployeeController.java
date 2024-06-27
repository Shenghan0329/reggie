package com.John.reggie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.John.reggie.Common.R;
import com.John.reggie.entity.Employee;
import com.John.reggie.service.EmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest req, @RequestBody Employee e){
        // Encode password
        String password = e.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // Get Employee
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, e.getUsername());
        Employee employee = employeeService.getOne(queryWrapper);

        // If name not in database
        if(employee == null) 
            return R.error("User Not Exist");

        // If password incorrect
        if(!employee.getPassword().equals(password)) 
            return R.error("Password Incorrect");
            
        // If status = 0
        if(employee.getStatus() == 0)
            return R.error("User Blocked");

        // Success
        req.getSession().setAttribute("employee", employee.getId());
        return R.success(employee);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest req){
        req.getSession().removeAttribute("employee");
        return R.success("Logout Success");
    }

    @PostMapping
    public R<String> add(HttpServletRequest req, @RequestBody Employee e){

        // Add missing fields
        e.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        log.info("ADD Employee {}",e.toString());

        boolean success = employeeService.save(e);
        System.out.println(success);
        if(success)
            return R.success("Add Employee Success, id = {"+e.getId().toString()+"}");
        else
            return R.error("Add Employee Failed");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page = {}, pageSize = {}, name = {}",page,pageSize,name);

        // Get Employees
        // Page
        Page p = new Page<>(page,pageSize);
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        // Name
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName,name);
        // Order
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(p,queryWrapper);

        return R.success(p);
    }

    @PutMapping
    public R<String> update(HttpServletRequest req, @RequestBody Employee e){
        employeeService.updateById(e);
        return R.success("Info Update Success");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(HttpServletRequest req, @PathVariable Long id){
        Employee e = employeeService.getById(id);
        log.info("QUERY EMPLOYEE INFO");
        if(e!=null) return R.success(e);
        log.info("NOT FOUND: ID = {}",id);
        return R.error("NOT FOUND: ID = "+id);
    }
}
