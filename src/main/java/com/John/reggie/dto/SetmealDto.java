package com.John.reggie.dto;

import com.John.reggie.entity.Setmeal;
import com.John.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
