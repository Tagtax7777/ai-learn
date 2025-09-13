package com.tagtax.mapper;

import com.tagtax.entity.User;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface UserMapper {
    User queryUserByPhone(String phone);

    User queryUserByUsername(String username);

    Integer addOneUser(User user);

    User queryUserById(Long id);
}
