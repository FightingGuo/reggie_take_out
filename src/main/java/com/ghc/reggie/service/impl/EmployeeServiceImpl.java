package com.ghc.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghc.reggie.bean.Employee;
import com.ghc.reggie.mapper.EmployeeMapper;
import com.ghc.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/7/23 - 23:31
 */
@Slf4j
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService{

}
