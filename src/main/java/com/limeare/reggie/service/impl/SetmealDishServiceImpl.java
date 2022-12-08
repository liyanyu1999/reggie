package com.limeare.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.limeare.reggie.common.R;
import com.limeare.reggie.dto.DishDto;
import com.limeare.reggie.dto.SetmealDto;
import com.limeare.reggie.entity.DishFlavor;
import com.limeare.reggie.entity.SetmealDish;
import com.limeare.reggie.mapper.SetmealDishMapper;
import com.limeare.reggie.service.SetmealDishService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {

    //获取套餐菜品信息
    @Override
    public List<SetmealDish> getSetmealDishBySetmealId(Long id) {

        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        queryWrapper.orderByDesc(SetmealDish::getPrice);

        List<SetmealDish> list = this.list(queryWrapper);

        return list;
    }

}
