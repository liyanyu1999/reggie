package com.limeare.reggie.dto;

import com.limeare.reggie.entity.Setmeal;
import com.limeare.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
