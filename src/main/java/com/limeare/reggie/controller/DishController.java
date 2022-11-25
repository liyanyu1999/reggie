package com.limeare.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.limeare.reggie.common.R;
import com.limeare.reggie.dto.DishDto;
import com.limeare.reggie.entity.Category;
import com.limeare.reggie.entity.Dish;
import com.limeare.reggie.service.CategoryService;
import com.limeare.reggie.service.DishFlavorService;
import com.limeare.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    //新增菜品
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }


    //分页
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Dish> pageInfo = new Page(page,pageSize);
        Page<DishDto> dishDtoPage=new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        //条件
        queryWrapper.like(name!=null,Dish::getName,name);

        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,queryWrapper);

        //拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        //获取分类名称
        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list=records.stream().map((item)->{
            DishDto dishDto=new DishDto();
            //拷贝
            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();
            //通过id查询分类
            Category category = categoryService.getById(categoryId);

            if (category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    //通过id查询菜品信息和口味信息
    @GetMapping ("/{id}")
    public R<DishDto> get(@PathVariable Long id){

        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    //修改菜品信息
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){

        dishService.updateWithFlavor(dishDto);

        return R.success("菜品信息修改成功");
    }

    //停售 起售 菜品
    @PostMapping("/status/{state}")
    public R<String> status(@PathVariable int state ,@RequestParam List<Long> ids){
        log.info("停售菜品");
        dishService.updateWithStatus(state,ids);
        return R.success("菜品已停售");
    }

    //查询菜品数据
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){

        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper();
        //查询条件
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);

        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        return R.success(list);
    }

    //删除菜品
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        dishService.removeWithFlavor(ids);
        return R.success("菜品删除成功");
    }

}
