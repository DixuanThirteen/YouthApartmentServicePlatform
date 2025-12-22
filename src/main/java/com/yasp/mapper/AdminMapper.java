package com.yasp.mapper;

import com.yasp.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper {
    Admin selectByUsername(String username);

    int insertAdmin(String username,String password);
}
