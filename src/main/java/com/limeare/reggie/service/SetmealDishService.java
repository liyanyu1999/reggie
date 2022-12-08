package com.limeare.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.limeare.reggie.common.R;
import com.limeare.reggie.dto.DishDto;
import com.limeare.reggie.dto.SetmealDto;
import com.limeare.reggie.entity.DishFlavor;
import com.limeare.reggie.entity.SetmealDish;

import java.util.List;

public interface SetmealDishService extends IService<SetmealDish> {

    public List<SetmealDish> getSetmealDishBySetmealId(Long id);
}
