package com.limeare.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.limeare.reggie.common.CustomerException;
import com.limeare.reggie.dto.DishDto;
import com.limeare.reggie.entity.Dish;
import com.limeare.reggie.entity.DishFlavor;
import com.limeare.reggie.entity.SetmealDish;
import com.limeare.reggie.mapper.DishMapper;
import com.limeare.reggie.service.DishFlavorService;
import com.limeare.reggie.service.DishService;
import com.limeare.reggie.service.SetmealDishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private SetmealDishService setmealDishService;

    //新增菜品  及口味
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品
        this.save(dishDto);
        Long dishId = dishDto.getId();

        //保存口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

    }

    //通过id查询菜品信息和口味信息
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品
        Dish dish = this.getById(id);

        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询口味
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(flavors);

        return dishDto;
    }

    //更新菜品信息，口味信息
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品的口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        //添加口味

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

    }

    //停售菜品
    @Override
    public void updateWithStatus(int state,List<Long> ids) {
        //update dish set status=state where id in ids
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();

        //是否有套餐包含了该菜品
//        LambdaQueryWrapper<SetmealDish> queryWrapper1=new LambdaQueryWrapper<>();
//        queryWrapper1.in(SetmealDish::getDishId,ids);
//        int count1 = setmealDishService.count(queryWrapper1);
//        if (count1>0){
//            throw new CustomerException("有套餐包含该菜品，不能停售");
//        }

        queryWrapper.in(Dish::getId,ids);
        Dish dish=new Dish();
        dish.setStatus(state);

        this.saveOrUpdate(dish,queryWrapper);

    }

    //删除菜品  同时删除口味和菜品的关联
    @Override
    public void removeWithFlavor(List<Long> ids) {
        //查询状态是否可以删除
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        queryWrapper.eq(Dish::getStatus,1);

        int count = this.count(queryWrapper);
        if (count>0){
            throw new CustomerException("有菜品正在售卖中，不能删除");
        }

        //是否有套餐包含了该菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getDishId,ids);
        int count1 = setmealDishService.count(queryWrapper1);
        if (count1>0){
            throw new CustomerException("有套餐包含该菜品，不能删除");
        }


        //删除菜品
        this.removeByIds(ids);
        //删除菜品口味数据
        LambdaQueryWrapper<DishFlavor> queryWrapper2=new LambdaQueryWrapper<>();
        queryWrapper2.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapper2);
    }
}
