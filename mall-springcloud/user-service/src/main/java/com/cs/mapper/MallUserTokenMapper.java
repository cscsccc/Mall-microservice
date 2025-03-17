package com.cs.mapper;

import com.cs.pojo.MallUserToken;
import org.apache.ibatis.annotations.Param;

public interface MallUserTokenMapper {
    MallUserToken selectByPrimaryKey(@Param("userId") Long userId);

    int updateByPrimaryKey(MallUserToken mallUserToken);

    int insertByPrimaryKey(MallUserToken mallUserToken);

    MallUserToken selectByToken(@Param("token") String token);

    int deleteByPrimaryKey(@Param("userId") Long userId);
}
