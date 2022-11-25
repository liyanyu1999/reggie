package com.limeare.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.limeare.reggie.common.R;
import com.limeare.reggie.entity.Category;
import com.limeare.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    //新增分类
    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("新增成功");
    }

    //分页查询
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //  分页构造器
        Page<Category> pageInfo = new Page(page,pageSize);

        //  条件构造器
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);

        categoryService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    //删除分类
    @DeleteMapping
    public R<String> delete(Long ids){

        //  categoryService.removeById(ids);
        categoryService.remove(ids);

        return R.success("分类信息删除成功");
    }

    //修改分类
    @PutMapping
    public R<String> update(@RequestBody Category category, HttpServletRequest request){

        categoryService.updateById(category);

        return R.success("分类修改成功");
    }

    //根据条件 查询分类数据
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper =new LambdaQueryWrapper<>();

        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list=categoryService.list(queryWrapper);

        return R.success(list);
    }
}
