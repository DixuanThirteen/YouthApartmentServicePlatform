package com.yasp.mapper;

import com.yasp.entity.ProviderAccount;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProviderAccountMapper {
    int insertProviderAccount(ProviderAccount providerAccount);

    ProviderAccount selectProviderAccountByUsername(String username);
}
