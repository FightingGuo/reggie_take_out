package com.ghc.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghc.reggie.bean.AddressBook;
import com.ghc.reggie.mapper.AddressBookMapper;
import com.ghc.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/8/3 - 17:42
 */
@Service
@Slf4j
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
