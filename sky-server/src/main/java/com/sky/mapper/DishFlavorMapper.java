package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author: FANGYUN
 * @date: 2024-07-26 19:19
 * @description:
 */
@Mapper
public interface DishFlavorMapper {
    void addFlavor(List<DishFlavor> flavors);
}
