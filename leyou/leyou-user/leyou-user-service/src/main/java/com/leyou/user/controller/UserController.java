package com.leyou.user.controller;

import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * @author: suhai
 * @create: 2020-10-12 13:56
 */
@Controller
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 注册
     * 实现用户数据的校验，主要包括对：手机号、用户名的唯一性校验。
     * @param data  要校验的数据 String
     * @param type  要校验的数据类型：1，用户名；2，手机 Integer
     * @return
     */
    @GetMapping("check/{data}/{type}")
    public ResponseEntity<Boolean> checkUser(
            @PathVariable("data") String data,
            @PathVariable("type") Integer type
    ) {
        Boolean bool = userService.checkUser(data, type);
        if (bool == null) {
            return ResponseEntity.badRequest().build();
        }
        //返回存在与否
        return ResponseEntity.ok(bool);
    }

    /**
     * 发送验证码给短信微服务
     * @param phone
     * @return
     */
    @PostMapping("code")
    public ResponseEntity<Void> sendVerifyCode(@RequestParam("phone") String phone) {
        if (!StringUtils.isNoneBlank(phone)) {
            return ResponseEntity.badRequest().build();
        }
        userService.sendVerifyCode(phone);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 注册用户
     * @param user
     * @param code
     * @return
     */
    @PostMapping("register")
    public ResponseEntity<Void> register(@Valid User user, @RequestParam("code") String code) {
        if (user == null || !StringUtils.isNoneBlank(code)) {
            return ResponseEntity.badRequest().build();
        }
        userService.register(user, code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    @GetMapping("query")
    public ResponseEntity<User> queryUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) {
        User user = userService.queryUser(username, password);
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(user);
    }
}
