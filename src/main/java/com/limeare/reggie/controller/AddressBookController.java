package com.limeare.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.limeare.reggie.common.BaseContext;
import com.limeare.reggie.common.R;
import com.limeare.reggie.entity.AddressBook;
import com.limeare.reggie.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    //新增地址
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook){

        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);

        return R.success(addressBook);
    }

    //设置默认地址
    @PutMapping("default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook){

        LambdaUpdateWrapper<AddressBook> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        updateWrapper.set(AddressBook::getIsDefault,0);

        addressBookService.update(updateWrapper);
        //设置默认
        addressBook.setIsDefault(1);

        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    //根据id查询地址
    @GetMapping("/{id}")
    public R get(@PathVariable Long id){
        AddressBook addressBook=addressBookService.getById(id);
        if (addressBook != null){
            return R.success(addressBook);
        }else {
            return R.error("没有找到");
        }
    }

    //查询默认地址
    @GetMapping("default")
    public R<AddressBook> getDefault(){

        LambdaQueryWrapper<AddressBook> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault,1);

        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if(addressBook == null){
            return R.error("没有找到");
        }else {
            return R.success(addressBook);
        }
    }

    //查询用户所有的地址
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());

        LambdaQueryWrapper<AddressBook> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(null != addressBook.getUserId(),AddressBook::getUserId,addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        return R.success(addressBookService.list(queryWrapper));
    }

    //修改地址信息

    public R<String> update(){


        return R.success("地址信息修改成功");
    }


}
