package com.John.reggie.controller;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @SuppressWarnings("rawtypes")
    @Autowired
    private RedisTemplate redisTemplate;

    @SuppressWarnings("unchecked")
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

            // // Store Message into Session
            // session.setAttribute(phone, code);

            // Store message in redis
            redisTemplate.opsForValue().set(phone,code,5,TimeUnit.MINUTES);
            
            return R.success("SMS Success");
        }
        return R.error("SMS Fail");
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/login")
    public R<User> login(@SuppressWarnings("rawtypes") @RequestBody Map map, HttpSession session){
        // Get phone number
        String phone = map.get("phone").toString();
        // Get code from map
        String code = map.get("code").toString();

        // // Get code from local session
        // Object codeInSession = session.getAttribute(phone);

        // Get code from redis
        Object codeInSession = redisTemplate.opsForValue().get(phone);
        
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
            // Delete code from redis
            redisTemplate.delete(phone);
            return R.success(user);
        }

        return R.error("Login Fail");
    }
    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest req){
        req.getSession().removeAttribute("user");
        return R.success("Logout Success");
    }
    @GetMapping("/{phone}")
    public R<User> getByPhone(@PathVariable String phone){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,phone);
        User user = userService.getOne(queryWrapper);
        return R.success(user);
    }
    @PutMapping
    public R<String> update(@RequestBody User user){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,user.getPhone());
        userService.update(user,queryWrapper);
        return R.success("Update User Success");
    }
}
