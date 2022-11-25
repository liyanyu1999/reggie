package com.limeare.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.limeare.reggie.entity.User;
import com.limeare.reggie.mapper.UserMapper;
import com.limeare.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
