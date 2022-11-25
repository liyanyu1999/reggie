package com.limeare.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.limeare.reggie.common.R;
import com.limeare.reggie.dto.DishDto;
import com.limeare.reggie.dto.SetmealDto;
import com.limeare.reggie.entity.Category;
import com.limeare.reggie.entity.Dish;
import com.limeare.reggie.entity.Setmeal;
import com.limeare.reggie.entity.SetmealDish;
import com.limeare.reggie.service.CategoryService;
import com.limeare.reggie.service.SetmealDishService;
import com.limeare.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    //新增套餐
    @PostMapping
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
    public R<String> status(@PathVariable int state,@RequestParam List<Long> ids){

        setmealService.updateWithStatus(state,ids);
        return R.success("套餐已停售");
    }

    //删除套餐
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){

        setmealService.removeWithDish(ids);

        return R.success("套餐删除成功");
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);

        return R.success("套餐信息修改成功");
    }

}
