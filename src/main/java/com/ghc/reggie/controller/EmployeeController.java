package com.ghc.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghc.reggie.bean.Employee;
import com.ghc.reggie.service.EmployeeService;
import com.ghc.reggie.utils.R;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/7/23 - 23:29
 */
@Slf4j
@RestController
@ResponseBody
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request 用来存员工id放在session中，
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        /**
         * 2、根据页面提交的用户名username查询数据库
         * 3、如果没有查询到则返回登录失败结果
         * 4、密码比对，如果不一致则返回登录失败结果
         * 5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
         * 6、登录成功，将员工id存入Session并返回登录成功结果
         */

        //1、将页面提交的代码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、创建查询对象
        LambdaQueryWrapper<Employee> usernamelqw=new LambdaQueryWrapper<>();
        usernamelqw.eq(Employee::getUsername,employee.getUsername());

        Employee emp = employeeService.getOne(usernamelqw);

        //3、如果没有查询到则返回登录失败结果
        if (emp==null){
            return R.error("登录失败，用户名不存在");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)){
            return R.error("登录失败，密码错误");
        }
        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus()==0){
            return R.error("员工已被禁用");
        }


        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());

        return R.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");

        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee,HttpServletRequest request){
        //设置初始密码为123666，用MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123666".getBytes()));

//        employee.setCreateTime(LocalDateTime.now());//获取系统当前时间
//        employee.setUpdateTime(LocalDateTime.now());//获取系统更新时间
//        //获取当前登录用户的id
//        Long currentId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(currentId);
//        employee.setUpdateUser(currentId);

        employeeService.save(employee);
        return R.success("保存成功");
    }

    @GetMapping("/page")
    public R<Page> page(Integer page,Integer pageSize,String name){

//        log.info("page={} pageSize={} name={}",page,pageSize,name);

        //构造分页构造器
        Page<Employee> pg=new Page<>(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> lqw=new LambdaQueryWrapper<>();
        //添加过滤条件
        lqw.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        lqw.orderByAsc(Employee::getUpdateTime);


        //执行查询
        employeeService.page(pg, lqw);
        //查询完后会自动封装在Page对象里

        return R.success(pg);
    }


    /**
     * 根据id修改员工状态
     * @param employee
     * @return
     */
    @PutMapping
    public R updateEmpStatus(@RequestBody Employee employee,HttpServletRequest request){
//        log.info("employee= {}",employee);

//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
//        long id = Thread.currentThread().getId();
//        log.info("线程id为={}",id);

        employeeService.updateById(employee);
        return R.success("修改成功");
    }

    @GetMapping("/{id}")
    public R getEmpById(@PathVariable Long id){
//        log.info("id {}",id);
        //首先根据id查找对应员工信息，返回
        Employee employee = employeeService.getById(id);

        if (employee!=null){
            return R.success(employee);
        }
        else {
            return R.error("没有查询到对应员工信息");
        }
    }


}
