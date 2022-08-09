package com.ghc.reggie.dto;

import com.ghc.reggie.bean.Setmeal;
import com.ghc.reggie.bean.SetmealDish;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.FutureTask;

@Data
@ApiModel("套餐dto")
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;

}
