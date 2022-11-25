package com.limeare.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.limeare.reggie.common.CustomerException;
import com.limeare.reggie.entity.Category;
import com.limeare.reggie.entity.Dish;
import com.limeare.reggie.entity.Setmeal;
import com.limeare.reggie.mapper.CategoryMapper;
import com.limeare.reggie.service.CategoryService;
import com.limeare.reggie.service.DishService;
import com.limeare.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

//    根据id删除分类
//    删除之前进行判断
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper =new LambdaQueryWrapper<>();
        //添加查询条件
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper);

        //判断分类是否关联了菜品
        if (count1>0){
            //关联了菜品不能删除
            throw  new CustomerException("当前分类下关联了菜品，不能删除");

        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper =new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);

        // 判断分类是否关联了套餐
        if (count2>0){
            //关联了套餐不能删除
            throw  new CustomerException("当前分类下关联了套餐，不能删除");

        }

        //正常删除
        super.removeById(id);
    }
}
