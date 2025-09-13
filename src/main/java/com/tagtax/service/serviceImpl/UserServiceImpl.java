package com.tagtax.service.serviceImpl;

import com.tagtax.entity.Result;
import com.tagtax.entity.User;
import com.tagtax.mapper.StudyTimeMapper;
import com.tagtax.mapper.UserMapper;
import com.tagtax.service.UserService;
import com.tagtax.utils.JwtUtil;
import com.tagtax.utils.SMSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StudyTimeMapper studyTimeMapper;

    @Autowired
    private SMSUtil smsUtil;

    @Override
    public Result checkSms(String phone, String sms) {
        boolean result = smsUtil.validateSMS(phone, sms);
        // 验证码正确时
        if(result){
            // 用户存在时(执行登录)
            if(userMapper.queryUserByPhone(phone) != null){
                User user = userMapper.queryUserByPhone(phone);
                // 生成JWT令牌
                String token = JwtUtil.generateToken(
                        user.getId(),
                        user.getUsername(),
                        user.getPhone()
                );
                Map<String, Object> response = new HashMap<>();
                response.put("user", user);
                response.put("token", token);
                return Result.success(response);
            }else {
                // 用户不存在时（注册）
                User user = new User();
                user.setUsername("新用户");
                user.setPhone(phone);
                if(userMapper.addOneUser(user) != null){
                    User user1 = userMapper.queryUserByPhone(phone);
                    // 生成JWT令牌
                    String token = JwtUtil.generateToken(
                            user1.getId(),
                            user1.getUsername(),
                            user1.getPhone()
                    );
                    Map<String, Object> response = new HashMap<>();
                    response.put("user", user1);
                    response.put("token", token);
                    // 添加此用户学习时长表的字段
                    studyTimeMapper.createUserStudyTime(user1.getId());
                    return Result.success(response);
                }else {
                    return Result.error("注册失败，请重试");
                }
            }
        }else {
            return Result.error("验证码错误");
        }
    }
}
