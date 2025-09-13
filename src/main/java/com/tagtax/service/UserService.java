package com.tagtax.service;

import com.tagtax.entity.Result;

public interface UserService{

    Result checkSms(String phone, String sms);
}
