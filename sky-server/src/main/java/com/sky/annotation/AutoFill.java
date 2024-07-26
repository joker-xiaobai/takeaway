package com.sky.annotation;

/**
 * @author: FANGYUN
 * @date: 2024-07-25 16:47
 * @description:
 */

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，对公共字段create，update进行赋值
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    //当数据库操作类型为insert，update时，要进行一些操作
    OperationType value();

}

