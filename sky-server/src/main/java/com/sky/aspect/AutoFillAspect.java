package com.sky.aspect;

/**
 * @author: FANGYUN
 * @date: 2024-07-25 16:53
 * @description:
 */

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面类，让加了@Autofill注解的方法都调用这个类的方法
 */

@Aspect
@Component//加入到spring容器中
@Slf4j//生成日志
public class AutoFillAspect {
    /**
     * 切入点：对哪些类的哪些方法进行拦截
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..))&&@annotation(com.sky.annotation.AutoFill)")//mapper下的接口中加了@Autofill注解的方法，要调用该方法
    public void autoFillPointCut(){};


    /**
     * 前置操作，为对应公共字段赋值
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){//joinpoint连接点，获取拦截到的方法和参数
        log.info("开始进行公共字段的填充");
        //获取被拦截方法的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//获取签名对象
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);//获取方法上的注解对象
        OperationType type = annotation.value();//获取数据库操作类型
        //获取参数，参数是实体对象
        Object[] args = joinPoint.getArgs();//获取所有参数
        if(args==null||args.length==0)return;
        Object arg = args[0];//规定第一个参数放实体对象
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        //对公共属性赋值，通过反射
        if(type==OperationType.INSERT){//枚举类可以用==判断
            try {
                Method setCreateUser = arg.getClass().getDeclaredMethod("setCreateUser", Long.class);//方法名，参数类型
                Method setCreateTime = arg.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class);
                Method setUpdateUser = arg.getClass().getDeclaredMethod("setUpdateUser", Long.class);
                Method setUpdateTime = arg.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
                setCreateUser.invoke(arg,currentId);//对象，参数值
                setCreateTime.invoke(arg,now);
                setUpdateUser.invoke(arg,currentId);
                setUpdateTime.invoke(arg,now);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            try {
                Method setUpdateUser = arg.getClass().getDeclaredMethod("setUpdateUser", Long.class);
                Method setUpdateTime = arg.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
                setUpdateUser.invoke(arg,currentId);
                setUpdateTime.invoke(arg,now);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}
