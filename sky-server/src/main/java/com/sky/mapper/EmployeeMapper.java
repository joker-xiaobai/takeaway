package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     *
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    Page<Employee> pageEmployee(EmployeePageQueryDTO employeePageQueryDTO);

    @AutoFill(OperationType.UPDATE)
    void updateInfo(Employee employee);

    @AutoFill(OperationType.INSERT)
    @Insert("insert into employee(name,username,password,sex,id_number,phone,create_time,update_time,create_user,update_user,status)"
        +"values "+"(#{name},#{username},#{password},#{sex},#{idNumber},#{phone},#{createTime},#{updateTime},#{createUser},#{updateUser},#{status})"
    )
    void save(Employee employee);

    /**
     * 根据id查询员工信息，回显
     * @param id
     * @return
     */
    @Select("select * from employee where id=#{id}")
    Employee getById(Long id);
}
