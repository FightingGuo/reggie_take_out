package com.ghc.reggie.dto;

import com.ghc.reggie.bean.Dish;
import com.ghc.reggie.bean.DishFlavor;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("菜品dto")
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
