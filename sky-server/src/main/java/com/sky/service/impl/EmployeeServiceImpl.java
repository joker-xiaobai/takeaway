package com.sky.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
         password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    @Override
    public PageResult pageEmployee(EmployeePageQueryDTO employeePageQueryDTO) {
        //开始分页查询
        //用pagehelper插件,不能用lease项目中的page和Ipage，这是mybatisplus中的
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());//在这里写了，就能在sql语句中动态加上limit
        //因为pagehelper实质是基于threadlocal，同一个线程可以直接使用
        Page<Employee> page= employeeMapper.pageEmployee(employeePageQueryDTO);//因为查询的是employee对象
        long total = page.getTotal();//得到总的记录数
        List<Employee> result = page.getResult();//得到查询到的记录集合
        PageResult pageResult = new PageResult();
        pageResult.setTotal(total);
        pageResult.setRecords(result);
        return pageResult;
    }

    @Override
    public void changeStatus(Long id, Integer status) {
        Employee employee = new Employee();
        employee.setStatus(status);
        employee.setId(id);
        employeeMapper.updateInfo(employee);//这是一个通用的修改方法，不仅是修改帐号状态
    }

    @Override
    public void save(Employee employee) {
        employeeMapper.save(employee);
    }

    @Override
    public Employee getById(Long id) {
        Employee employee= employeeMapper.getById(id);
        employee.setPassword("****");
        return employee;
    }

    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setCreateUser(BaseContext.getCurrentId());//登录人的id在interceptor中已经set了
        employeeMapper.updateInfo(employee);
    }

}
