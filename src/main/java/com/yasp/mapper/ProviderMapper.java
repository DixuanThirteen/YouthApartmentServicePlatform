package com.yasp.mapper;

import com.yasp.entity.Provider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProviderMapper {
    Provider SelectProviderByName (@Param("name") String name);

    Provider SelectProviderByLicense(@Param("licenseNumber") String licenseNumber);

    int insertProvider(Provider provider);

    int updateProvider(Provider provider);
}
