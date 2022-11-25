package com.limeare.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.limeare.reggie.dto.SetmealDto;
import com.limeare.reggie.entity.Setmeal;
import com.limeare.reggie.entity.SetmealDish;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    public void saveWithDish(SetmealDto setmealDto);

    public void removeWithDish(List<Long> ids);

    public void updateWithStatus(int state,List<Long> ids);

    public SetmealDto getByIdWithDish(Long id);

    public void updateWithDish(SetmealDto setmealDto);
}
