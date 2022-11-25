package com.limeare.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.limeare.reggie.common.CustomerException;
import com.limeare.reggie.dto.SetmealDto;
import com.limeare.reggie.entity.Setmeal;
import com.limeare.reggie.entity.SetmealDish;
import com.limeare.reggie.mapper.SetmealMapper;
import com.limeare.reggie.service.SetmealDishService;
import com.limeare.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    //新增套餐，同时保存套餐和菜品的关联关系
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐信息
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
           item.setSetmealId(setmealDto.getId());
           return item;
        }).collect(Collectors.toList());
        //保存套餐和菜品的关联信息
        setmealDishService.saveBatch(setmealDishes);

    }

    //删除套餐 同时删除套餐和菜品的关联关系
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态 是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);
        if (count>0){
            throw new CustomerException("有套餐正在售卖中，不能删除");
        }
        //删除套餐数据
        this.removeByIds(ids);

        //删除关联数据
        LambdaQueryWrapper<SetmealDish> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper1);

    }

    //停售 起售套餐
    @Override
    public void updateWithStatus(int state, List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        Setmeal setmeal=new Setmeal();
        setmeal.setStatus(state);

        this.saveOrUpdate(setmeal,queryWrapper);

    }

    //通过id查询套餐信息和菜品信息
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        //查询套餐
        Setmeal setmeal=this.getById(id);

        SetmealDto setmealDto=new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        //查询菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> dishes = setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(dishes);

        return setmealDto;
    }

    //更新套餐菜品信息
    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        //更新 套餐表信息
        this.updateById(setmealDto);

        //清楚当前套餐菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());

        setmealDishService.remove(queryWrapper);

        //添加菜品
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
           item.setSetmealId(setmealDto.getId());
           return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }
}
