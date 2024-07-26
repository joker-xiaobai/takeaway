package com.sky.service;

import com.sky.dto.DishDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author: FANGYUN
 * @date: 2024-07-26 16:32
 * @description:
 */

public interface DishService {

    void addDishFlavor(DishDTO dishDTO);
}
