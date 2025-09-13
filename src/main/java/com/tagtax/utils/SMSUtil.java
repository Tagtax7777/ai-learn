package com.tagtax.utils;

import cn.hutool.captcha.generator.RandomGenerator;
import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
public class SMSUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    public String generateSMS(String phone) {
        // 生产环境请求地址
        String serverIp = "app.cloopen.com";
        // 请求端口
        String serverPort = "8883";
        String accountSId = "2c94811c9860a9c40198dac65d8913e1";
        String accountToken = "b1d69c11c1a4461aa01afd640a49a703";
        // 创建应用的appID
        String appId = "2c94811c9860a9c40198dac65f0b13e8";
        CCPRestSmsSDK sdk = new CCPRestSmsSDK();
        sdk.init(serverIp, serverPort);
        sdk.setAccount(accountSId, accountToken);
        sdk.setAppId(appId);
        sdk.setBodyType(BodyType.Type_JSON);
        String to = phone;
        String templateId = "1";
        // 4位随机数（采用hutoll工具）
        RandomGenerator randomGenerator = new RandomGenerator("0123456789", 4);
        String randomNum = randomGenerator.generate();
        Long expire = 2l; // 有效期
        String[] datas = {randomNum, expire.toString()};
        // 发送
        HashMap<String, Object> result = sdk.sendTemplateSMS(to, templateId, datas);
        // 结果
        if("000000".equals(result.get("statusCode"))){
            // 存入redis，设置有效期60s;
            redisTemplate.opsForValue().set(phone, randomNum);
            redisTemplate.expire(phone, 60L, TimeUnit.SECONDS);
            return randomNum;
        }else {
            return null;
        }
    }

    public boolean validateSMS(String phone, String sms) {
        // 取得redis里面的验证码
        Object obj = redisTemplate.opsForValue().get(phone);
        if(Objects.isNull(obj)){
            throw new RuntimeException("验证码已过期");
        }
        String sms_redis = (String) obj;
        return sms_redis.equals(sms);
    }
}
