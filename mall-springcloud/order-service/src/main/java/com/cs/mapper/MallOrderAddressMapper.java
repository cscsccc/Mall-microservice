package com.cs.mapper;

import com.cs.entity.po.MallOrderAddress;
import org.apache.ibatis.annotations.Param;

public interface MallOrderAddressMapper {
    int insertSelective(MallOrderAddress mallOrderAddress);
}
