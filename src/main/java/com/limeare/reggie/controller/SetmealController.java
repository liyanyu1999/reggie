package com.limeare.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.limeare.reggie.common.R;
import com.limeare.reggie.dto.DishDto;
import com.limeare.reggie.dto.SetmealDto;
import com.limeare.reggie.entity.*;
import com.limeare.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;

    //新增套餐
    @PostMapping
    @CacheEvict(value = "setmealCache" , allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("套餐新增成功");
    }


    //分页
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){

        Page<Setmeal> pageInfo = new Page(page,pageSize);
        Page<SetmealDto> setmealDtoPage=new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper();

        queryWrapper.like(name!=null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo,queryWrapper);

        //拷贝   records
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");

        //获取分类名称
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list=records.stream().map((item)->{
            SetmealDto setmealDto=new SetmealDto();
            //拷贝
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId=item.getCategoryId();
            //分类名称
            Category category = categoryService.getById(categoryId);

            if (category!=null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }

            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(list);

        return R.success(setmealDtoPage);
    }

    //通过id查询套餐信息和菜品信息
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){
        SetmealDto setmealDto=setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    //停售 起售 套餐
    @PostMapping("/status/{state}")
    @CacheEvict(value = "setmealCache" , allEntries = true)
    public R<String> status(@PathVariable int state,@RequestParam List<Long> ids){

        setmealService.updateWithStatus(state,ids);
        return R.success("套餐已停售");
    }

    //删除套餐
    @DeleteMapping
    @CacheEvict(value = "setmealCache" , allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids){

        setmealService.removeWithDish(ids);

        return R.success("套餐删除成功");
    }

    //更新套餐
    @PutMapping
    @CacheEvict(value = "setmealCache" , allEntries = true)
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);

        return R.success("套餐信息修改成功");
    }

    //
    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId+'_'+#setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal){

        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }

    //
    @GetMapping("/dish/{id}")
    public R<List<DishDto>> dish(@PathVariable Long id){
        log.info(String.valueOf(id));

        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);

        List<SetmealDish> list = setmealDishService.list(queryWrapper);

        List<DishDto> dishDtoList= list.stream().map((item)->{

            DishDto dishDto=new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long dishId = item.getDishId();
            Dish dish = dishService.getById(dishId);
            BeanUtils.copyProperties(dish,dishDto);

            return dishDto;

        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }

}
