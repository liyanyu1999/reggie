package com.limeare.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.limeare.reggie.common.R;
import com.limeare.reggie.dto.DishDto;
import com.limeare.reggie.entity.Category;
import com.limeare.reggie.entity.Dish;
import com.limeare.reggie.entity.DishFlavor;
import com.limeare.reggie.service.CategoryService;
import com.limeare.reggie.service.DishFlavorService;
import com.limeare.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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

    @Autowired
    private RedisTemplate redisTemplate;

    //新增菜品
    @PostMapping
    @CacheEvict(value = "*" , allEntries = true)
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);

        Set keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);

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
    @CacheEvict(value = "*" , allEntries = true)
    public R<String> update(@RequestBody DishDto dishDto){

        dishService.updateWithFlavor(dishDto);

        Set keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);

        return R.success("菜品信息修改成功");
    }

    //停售 起售 菜品
    @PostMapping("/status/{state}")
    @CacheEvict(value = "*" , allEntries = true)
    public R<String> status(@PathVariable int state ,@RequestParam List<Long> ids){
        log.info("停售菜品");
        dishService.updateWithStatus(state,ids);

        Set keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);

        return R.success("菜品已停售");
    }


    //查询菜品数据
    @GetMapping("/list")
    @Cacheable(value = "dishCache",key = "#dish.categoryId+'_'+#dish.status")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList=null;

//        String key = "dish_"+dish.getCategoryId()+"_"+dish.getStatus();//dish_cid_status
//
//        //redis 获取缓存
//        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
//
//        if (dishDtoList!=null){
//            return R.success(dishDtoList);
//        }


        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper();
        //查询条件
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);

        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList=list.stream().map((item)->{
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

            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper1=new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper1);
            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());

//        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }


    //删除菜品
    @DeleteMapping
    @CacheEvict(value = "*" , allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids){
        dishService.removeWithFlavor(ids);

        Set keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);

        return R.success("菜品删除成功");
    }

}
