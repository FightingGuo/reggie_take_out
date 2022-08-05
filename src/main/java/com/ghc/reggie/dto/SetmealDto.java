package com.ghc.reggie.dto;

import com.ghc.reggie.bean.Setmeal;
import com.ghc.reggie.bean.SetmealDish;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.FutureTask;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;

}
