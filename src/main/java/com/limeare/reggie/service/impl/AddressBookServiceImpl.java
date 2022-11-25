package com.limeare.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.limeare.reggie.entity.AddressBook;
import com.limeare.reggie.mapper.AddressBookMapper;
import com.limeare.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
