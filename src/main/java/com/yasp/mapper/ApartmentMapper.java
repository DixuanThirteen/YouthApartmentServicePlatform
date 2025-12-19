package com.yasp.mapper;

import com.yasp.dto.ApartmentSearchRequest;
import com.yasp.entity.Apartment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApartmentMapper {

    Apartment selectById(@Param("id") Long id);

    List<Apartment> selectByProviderId(@Param("providerId") Long providerId);

    int insert(Apartment apartment);

    int updateById(Apartment apartment);

    int deleteById(@Param("id") Long id);

    List<Apartment> selectAll();

    List<Apartment> searchApartments(ApartmentSearchRequest req);

    Apartment selectByNameAndProviderId(String name, Long providerId);
}
