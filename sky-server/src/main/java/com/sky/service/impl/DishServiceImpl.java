package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author: FANGYUN
 * @date: 2024-07-26 16:33
 * @description:
 */

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    private DishFlavorMapper flavorMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品和对应的口味
     * @param dishDTO
     */
    @Transactional//菜品和口味要么一起添加，要么都不添加
    @Override
    public void addDishFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //向菜品表插入一个数据
        dishMapper.addFish(dish);//此时菜品id已经生成
        Long id = dish.getId();//insert进行主键回显了，可以直接get到id
        //向口味表插入多条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        for (DishFlavor f : flavors) {
            f.setDishId(id);//给每个口味都设置id  为啥不在xml中设置id呢
        }
        if (flavors != null && flavors.size() > 0) {
            dishFlavorMapper.addFlavor(flavors);//批量插入口味
        }
    }
}
