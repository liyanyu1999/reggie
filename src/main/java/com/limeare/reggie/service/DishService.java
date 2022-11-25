package com.limeare.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.limeare.reggie.dto.DishDto;
import com.limeare.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);

    public void updateWithStatus(int state,List<Long> ids);

    public void removeWithFlavor(List<Long> ids);

}
