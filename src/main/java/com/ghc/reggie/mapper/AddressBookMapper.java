package com.ghc.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ghc.reggie.bean.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/8/3 - 17:41
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
