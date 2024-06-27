package com.John.reggie.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.John.reggie.Common.R;
import com.John.reggie.entity.User;
import com.John.reggie.service.UserService;
import com.John.reggie.utils.SMSUtils;
import com.John.reggie.utils.ValidateCodeUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        // Get phone number
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            // Generate Code
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info(code);
            // // Send Message
            // SMSUtils.sendMessage(code, code, phone, code);
            // Store Message into Session
            session.setAttribute(phone, code);
            return R.success("SMS Success");
        }
        return R.error("SMS Fail");
    }

    @PostMapping("/login")
    public R<User> login(@SuppressWarnings("rawtypes") @RequestBody Map map, HttpSession session){
        // Get phone number
        String phone = map.get("phone").toString();
        // Get code from map
        String code = map.get("code").toString();
        // Get code from local session
        Object codeInSession = session.getAttribute(phone);
        // Determine if they match
        if(codeInSession != null && codeInSession.equals(code)){
            // If match, login success
            // Determine if the user is new, if new, register user
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);

            if(user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            return R.success(user);
        }

        return R.error("Login Fail");
    }

}
