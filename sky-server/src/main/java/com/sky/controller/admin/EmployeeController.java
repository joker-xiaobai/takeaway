package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 添加员工
     */
    @PostMapping
    public Result save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工，参数为：{}", employeeDTO);
        Employee employee=new Employee();
        //现有属性拷贝
        BeanUtils.copyProperties(employeeDTO,employee);
        employee.setStatus(StatusConstant.ENABLE);
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        //记录创建人和修改人id
        //todo:后期需要改为当前登录用户id，现在先写一个默认id
//        employee.setCreateUser(BaseContext.getCurrentId());
//        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeService.save(employee);//直接使用mp的insert，会使id使用雪花算法
        return Result.success();
    }

    /**
     * 分页查询员工信息
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){//param参数可以用实体类接收
        log.info("分页查询员工信息，参数为{}", employeePageQueryDTO);
        PageResult result=employeeService.pageEmployee(employeePageQueryDTO);
        return Result.success(result);
    }


    /**
     * 启用或者禁用账号
     * @param id
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    public Result changeStatus(@RequestParam Long id,@PathVariable Integer status){
        log.info("启用禁用账号，参数：{}，{}", id, status);
        employeeService.changeStatus(id,status);
        return Result.success();
    }


    /**
     * 根据id查询员工信息，回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息，参数：{}",id);
        Employee employee=employeeService.getById(id);
        return Result.success(employee);
    }

    @PutMapping
    public Result update(@RequestBody EmployeeDTO employeeDTO){//用employee接收完全可以的
        log.info("编辑员工信息{}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }
}
