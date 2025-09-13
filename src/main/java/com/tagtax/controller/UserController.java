package com.tagtax.controller;

import com.tagtax.entity.Result;
import com.tagtax.mapper.UserMapper;
import com.tagtax.service.UserService;
import com.tagtax.utils.JwtUtil;
import com.tagtax.utils.SMSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SMSUtil smsUtil;

    // 给用户发送手机验证码
    @PostMapping("/sendSms")
    public Result sendSms(@RequestParam("phone") String phone) {
        String sms = smsUtil.generateSMS(phone);
        if (sms == null) {
            return Result.error("验证码失效");
        }
        return Result.success(sms);
    }

    // 验证用户手机验证码(进行登录或注册操作)
    @PostMapping("/checkSms")
    public Result checkSms(@RequestParam("phone") String phone, @RequestParam("sms") String sms) {
        return userService.checkSms(phone, sms);
    }

    @GetMapping("/getUserInfo")
    public Result getUserInfo(@RequestHeader("Authorization") String token) {
        if(!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long userId = JwtUtil.getIdFromToken(token);
        return Result.success(userMapper.queryUserById(userId));
    }

    @GetMapping("/getUser")
    public Result getUser(@RequestHeader("Authorization") String token,
                          @RequestParam("userId")  Long userId) {
        if(!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        return Result.success(userMapper.queryUserById(userId));
    }







}
